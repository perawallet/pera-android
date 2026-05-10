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
import com.algorand.backup.domain.model.BackupApiError
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupManifest
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

        val manifest = when (val result = backupRepository.getManifest(backupId)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                if ((result.exception as? BackupApiError)?.isBackupDestroyed == true) {
                    return if (syncState.lastKnownBackupHash != null) {
                        PullSyncResult.BackupDestroyed
                    } else {
                        PullSyncResult.UpToDate
                    }
                }
                return PullSyncResult.Error(result.exception)
            }
        }

        if (manifest.backupGlobalHash == syncState.lastKnownBackupHash) {
            return PullSyncResult.UpToDate
        }

        val deltas = when (val result = backupRepository.getDeltas(backupId, syncState.lastSyncedSeq)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                if ((result.exception as? BackupApiError)?.isBackupDestroyed == true) {
                    return PullSyncResult.BackupDestroyed
                }
                return PullSyncResult.Error(result.exception)
            }
        }

        if (deltas.isEmpty()) {
            return handleEmptyDeltas(backupId, syncState, manifest)
        }

        return applyDeltas(backupId, syncState, manifest, deltas)
    }

    private suspend fun handleEmptyDeltas(
        backupId: BackupId,
        syncState: SyncState,
        backupManifest: BackupManifest
    ): PullSyncResult {
        if (syncState.lastKnownBackupHash == null && backupManifest.items.isNotEmpty()) {
            return populateFromManifest(backupId, backupManifest)
        }
        syncStateRepository.updateGlobalPointers(backupId, backupManifest.backupGlobalHash, syncState.lastSyncedSeq)
        return PullSyncResult.UpToDate
    }

    private suspend fun populateFromManifest(backupId: BackupId, backupManifest: BackupManifest): PullSyncResult {
        val activeKeys = backupManifest.items
            .filter { (_, item) -> item.status == BackupItemStatus.ACTIVE }
            .map { (key, item) ->
                syncStateRepository.updateItemState(backupId, key, syncItemStateMapper.mapFromManifestItem(item))
                key
            }
        return PullSyncResult.Updated(
            updatedKeys = activeKeys,
            deletedKeys = emptyList(),
            newSeq = backupManifest.lastSeq,
            backupGlobalHash = backupManifest.backupGlobalHash
        )
    }

    private suspend fun applyDeltas(
        backupId: BackupId,
        syncState: SyncState,
        backupManifest: BackupManifest,
        deltas: List<DeltaEntry>
    ): PullSyncResult {
        val updatedKeys = mutableListOf<BackupItemKey>()
        val deletedKeys = mutableListOf<BackupItemKey>()
        val reappearedKeys = mutableListOf<BackupItemKey>()

        for (delta in deltas) {
            when (delta.operation) {
                DeltaOperation.UPSERT -> applyUpsertDelta(backupId, syncState, delta, reappearedKeys)
                    ?.let { updatedKeys.add(it) }
                DeltaOperation.DELETE -> applyDeleteDelta(backupId, syncState, delta)
                    ?.let { deletedKeys.add(it) }
            }
        }

        val maxSeq = deltas.maxOf { it.seq }

        return PullSyncResult.Updated(
            updatedKeys = updatedKeys,
            deletedKeys = deletedKeys,
            reappearedKeys = reappearedKeys,
            newSeq = maxSeq,
            backupGlobalHash = backupManifest.backupGlobalHash
        )
    }

    private suspend fun applyUpsertDelta(
        backupId: BackupId,
        syncState: SyncState,
        delta: DeltaEntry,
        reappearedKeys: MutableList<BackupItemKey>
    ): BackupItemKey? {
        val existingItem = syncState.items[delta.key]
        val updatedItem = syncItemStateMapper.mapFromUpsertDelta(delta, existingItem)

        if (existingItem?.status == BackupItemStatus.IGNORED) {
            val ignoredItem = updatedItem.copy(status = BackupItemStatus.IGNORED)
            syncStateRepository.updateItemState(backupId, delta.key, ignoredItem)
            reappearedKeys.add(delta.key)
            return null
        }

        syncStateRepository.updateItemState(backupId, delta.key, updatedItem)
        return if (shouldDownloadPayload(existingItem, delta)) delta.key else null
    }

    private suspend fun applyDeleteDelta(
        backupId: BackupId,
        syncState: SyncState,
        delta: DeltaEntry
    ): BackupItemKey? {
        val existingItem = syncState.items[delta.key]
        if (existingItem != null) {
            val ignoredItem = syncItemStateMapper.mapFromDeleteDelta(delta, existingItem)
            syncStateRepository.updateItemState(backupId, delta.key, ignoredItem)
        }
        return if (existingItem?.status != BackupItemStatus.IGNORED) delta.key else null
    }

    private fun shouldDownloadPayload(existingItem: SyncItemState?, delta: DeltaEntry): Boolean {
        return when {
            existingItem?.status == BackupItemStatus.IGNORED -> false
            delta.status == BackupItemStatus.IGNORED -> false
            existingItem == null -> true
            else -> delta.hash != null && delta.hash != existingItem.lastRemoteHash
        }
    }

    private fun createInitialSyncState(backupId: BackupId): SyncState {
        return SyncState(backupId = backupId, lastKnownBackupHash = null, lastSyncedSeq = 0, items = emptyMap())
    }
}
