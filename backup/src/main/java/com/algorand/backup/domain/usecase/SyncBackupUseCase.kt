/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.backup.domain.usecase

import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PushSyncResult
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex

internal class SyncBackupUseCase @Inject constructor(
    private val getBackupId: GetBackupId,
    private val getBackupDeviceId: GetBackupDeviceId,
    private val pullAndImportSync: PullAndImportSync,
    private val pushBackupSync: PushBackupSync,
    private val preparePushPayloads: PreparePushPayloads,
    private val syncStateRepository: SyncStateRepository,
    private val backupRepository: BackupRepository,
    private val saveBackupSyncResult: SaveBackupSyncResult
) : SyncBackup {

    private val syncMutex = Mutex()

    override suspend fun invoke(): SyncBackupResult {
        if (!syncMutex.tryLock()) return SyncBackupResult.AlreadyRunning

        return try {
            val result = runSync()
            result.also { saveBackupSyncResult(it) }
        } finally {
            syncMutex.unlock()
        }
    }

    private suspend fun runSync(): SyncBackupResult {
        val backupId = getBackupId() ?: return SyncBackupResult.Error(IllegalStateException("No backup ID"))
        val deviceId = getBackupDeviceId() ?: return SyncBackupResult.Error(IllegalStateException("No device ID"))

        val pullResult = pullAndImportSync()
        if (pullResult is SyncBackupResult.BackupDestroyed) return pullResult

        val pushResult = pushLocalData(backupId, deviceId)
        when {
            pushResult is PushSyncResult.BackupDestroyed -> return SyncBackupResult.BackupDestroyed
            pushResult is PushSyncResult.Error && pullResult is SyncBackupResult.Error -> return pullResult
            pushResult is PushSyncResult.Error -> return SyncBackupResult.Error(pushResult.exception)
            pushResult is PushSyncResult.Pushed -> refreshManifestHash(backupId)
        }

        return checkPendingChanges(backupId)
    }

    private suspend fun pushLocalData(backupId: BackupId, deviceId: DeviceId): PushSyncResult {
        val encryptedPayloads = when (val result = preparePushPayloads(backupId)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return PushSyncResult.Error(result.exception)
        }

        return pushBackupSync(backupId, deviceId, encryptedPayloads)
    }

    private suspend fun refreshManifestHash(backupId: BackupId) {
        val manifest = when (val result = backupRepository.getManifest(backupId)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return
        }
        val currentState = syncStateRepository.getSyncState(backupId) ?: return
        syncStateRepository.updateGlobalPointers(backupId, manifest.backupGlobalHash, currentState.lastSyncedSeq)
    }

    private suspend fun checkPendingChanges(backupId: BackupId): SyncBackupResult {
        val hasDirtyItems = syncStateRepository.getDirtyItems(backupId).isNotEmpty()
        val hasPendingDeletes = syncStateRepository.getPendingDeletes(backupId).isNotEmpty()

        return if (hasDirtyItems || hasPendingDeletes) {
            SyncBackupResult.SuccessWithPendingChanges
        } else {
            SyncBackupResult.Success
        }
    }
}
