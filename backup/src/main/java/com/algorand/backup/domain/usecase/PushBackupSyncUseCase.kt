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
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PushSyncResult
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PushBackupSyncUseCase @Inject constructor(
    private val syncStateRepository: SyncStateRepository,
    private val pushDirtyBackupItems: PushDirtyBackupItems,
    private val deletePendingBackupItems: DeletePendingBackupItems
) : PushBackupSync {

    override suspend fun invoke(
        backupId: BackupId,
        deviceId: DeviceId,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PushSyncResult {
        val dirtyItems = syncStateRepository.getDirtyItems(backupId)
        val pendingDeletes = syncStateRepository.getPendingDeletes(backupId)

        if (dirtyItems.isEmpty() && pendingDeletes.isEmpty()) {
            return PushSyncResult.NothingToPush
        }

        val succeededKeys = mutableListOf<BackupItemKey>()
        val conflictedKeys = mutableListOf<BackupItemKey>()
        val deletedKeys = mutableListOf<BackupItemKey>()
        var maxSeq = 0L

        if (dirtyItems.isNotEmpty()) {
            when (val upsertResult = pushDirtyBackupItems(backupId, deviceId, dirtyItems, encryptedPayloads)) {
                is PeraResult.Success -> {
                    succeededKeys.addAll(upsertResult.data.succeededKeys)
                    conflictedKeys.addAll(upsertResult.data.conflictedKeys)
                    maxSeq = maxOf(maxSeq, upsertResult.data.maxSeq)
                }
                is PeraResult.Error -> return PushSyncResult.Error(upsertResult.exception)
            }
        }

        if (pendingDeletes.isNotEmpty()) {
            when (val deleteResult = deletePendingBackupItems(backupId, pendingDeletes)) {
                is PeraResult.Success -> {
                    deletedKeys.addAll(deleteResult.data.deletedKeys)
                    maxSeq = maxOf(maxSeq, deleteResult.data.maxSeq)
                }
                is PeraResult.Error -> return PushSyncResult.Error(deleteResult.exception)
            }
        }

        updateGlobalPointersIfNeeded(backupId, maxSeq)

        return PushSyncResult.Pushed(
            succeededKeys = succeededKeys,
            conflictedKeys = conflictedKeys,
            deletedKeys = deletedKeys
        )
    }

    private suspend fun updateGlobalPointersIfNeeded(backupId: BackupId, maxSeq: Long) {
        if (maxSeq <= 0) return
        val currentState = syncStateRepository.getSyncState(backupId) ?: return
        val lastKnownHash = currentState.lastKnownBackupHash ?: return
        if (maxSeq > currentState.lastSyncedSeq) {
            syncStateRepository.updateGlobalPointers(backupId, lastKnownHash, maxSeq)
        }
    }
}
