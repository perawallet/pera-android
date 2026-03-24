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

import com.algorand.backup.domain.mapper.SyncItemStateMapper
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.DeltaOperation
import com.algorand.backup.domain.model.PullSyncResult
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.model.SyncState
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PullBackupSyncUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper
) : PullBackupSync {

    override suspend operator fun invoke(backupId: BackupId): PullSyncResult {
        val syncState = syncStateRepository.getSyncState(backupId) ?: createInitialSyncState(backupId)

        val manifest = when (val manifestResult = backupRepository.getManifest(backupId)) {
            is PeraResult.Success -> manifestResult.data
            is PeraResult.Error -> return PullSyncResult.Error(manifestResult.exception)
        }

        if (manifest.backupGlobalHash == syncState.lastKnownBackupHash) {
            return PullSyncResult.UpToDate
        }

        val deltas = when (val deltasResult = backupRepository.getDeltas(backupId, syncState.lastSyncedSeq)) {
            is PeraResult.Success -> deltasResult.data
            is PeraResult.Error -> return PullSyncResult.Error(deltasResult.exception)
        }

        if (deltas.isEmpty()) {
            syncStateRepository.updateGlobalPointers(backupId, manifest.backupGlobalHash, syncState.lastSyncedSeq)
            return PullSyncResult.UpToDate
        }

        val updatedKeys = mutableListOf<BackupItemKey>()
        val deletedKeys = mutableListOf<BackupItemKey>()

        for (delta in deltas) {
            when (delta.operation) {
                DeltaOperation.UPSERT -> {
                    val existingItem = syncState.items[delta.key]
                    val updatedItem = syncItemStateMapper.mapFromUpsertDelta(delta, existingItem)
                    syncStateRepository.updateItemState(backupId, delta.key, updatedItem)

                    if (shouldDownloadPayload(existingItem, delta)) {
                        updatedKeys.add(delta.key)
                    }
                }
                DeltaOperation.DELETE -> {
                    val existingItem = syncState.items[delta.key]
                    if (existingItem != null) {
                        val ignoredItem = syncItemStateMapper.mapFromDeleteDelta(delta, existingItem)
                        syncStateRepository.updateItemState(backupId, delta.key, ignoredItem)
                    }
                    deletedKeys.add(delta.key)
                }
            }
        }

        val maxSeq = deltas.maxOf { it.seq }
        syncStateRepository.updateGlobalPointers(backupId, manifest.backupGlobalHash, maxSeq)

        return PullSyncResult.Updated(updatedKeys = updatedKeys, deletedKeys = deletedKeys)
    }

    private fun shouldDownloadPayload(existingItem: SyncItemState?, delta: DeltaEntry): Boolean {
        if (existingItem?.status == BackupItemStatus.IGNORED) return false
        if (delta.status == BackupItemStatus.IGNORED) return false
        if (existingItem == null) return true
        return delta.hash != null && delta.hash != existingItem.lastRemoteHash
    }

    private fun createInitialSyncState(backupId: BackupId): SyncState {
        return SyncState(
            backupId = backupId,
            lastKnownBackupHash = null,
            lastSyncedSeq = 0,
            items = emptyMap()
        )
    }
}
