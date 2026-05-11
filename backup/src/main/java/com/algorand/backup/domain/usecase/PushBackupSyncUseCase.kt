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

import com.algorand.backup.domain.model.BackupApiError
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.DeletedBackupItems
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PushSyncResult
import com.algorand.backup.domain.model.PushedDirtyBackupItems
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PushBackupSyncUseCase @Inject constructor(
    private val syncStateRepository: SyncStateRepository,
    private val pushDirtyBackupItems: PushDirtyBackupItems,
    private val deletePendingBackupItems: DeletePendingBackupItems,
    private val commitPushedItemsToSnapshot: CommitPushedItemsToSnapshot,
    private val evictDeletedItemsFromSnapshot: EvictDeletedItemsFromSnapshot,
    private val advanceBackupSyncCursor: AdvanceBackupSyncCursor
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

        val pushedDirty = when (val result = pushDirty(backupId, deviceId, dirtyItems, encryptedPayloads)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                if ((result.exception as? BackupApiError)?.isBackupDestroyed == true) {
                    return PushSyncResult.BackupDestroyed
                }
                return PushSyncResult.Error(result.exception)
            }
        }

        val pushedDeletes = when (val result = pushDeletes(backupId, pendingDeletes)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return PushSyncResult.Error(result.exception)
        }

        commitPushedItemsToSnapshot(pushedDirty.succeededKeys)
        evictDeletedItemsFromSnapshot(pushedDeletes.deletedKeys)
        advanceBackupSyncCursor(backupId, maxOf(pushedDirty.maxSeq, pushedDeletes.maxSeq))

        return PushSyncResult.Pushed(
            succeededKeys = pushedDirty.succeededKeys,
            conflictedKeys = pushedDirty.conflictedKeys,
            deletedKeys = pushedDeletes.deletedKeys
        )
    }

    private suspend fun pushDirty(
        backupId: BackupId,
        deviceId: DeviceId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PeraResult<PushedDirtyBackupItems> {
        if (dirtyItems.isEmpty()) return PeraResult.Success(EMPTY_PUSHED_DIRTY)
        return pushDirtyBackupItems(backupId, deviceId, dirtyItems, encryptedPayloads)
    }

    private suspend fun pushDeletes(
        backupId: BackupId,
        pendingDeletes: Map<BackupItemKey, SyncItemState>
    ): PeraResult<DeletedBackupItems> {
        if (pendingDeletes.isEmpty()) return PeraResult.Success(EMPTY_DELETED)
        return deletePendingBackupItems(backupId, pendingDeletes)
    }

    private companion object {
        val EMPTY_PUSHED_DIRTY = PushedDirtyBackupItems(emptyList(), emptyList())
        val EMPTY_DELETED = DeletedBackupItems(emptyList())
    }
}
