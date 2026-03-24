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
import com.algorand.backup.domain.model.BatchUpsertInput
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PushSyncResult
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.model.UpsertItemResult
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PushBackupSyncUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper
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

        if (dirtyItems.isNotEmpty()) {
            when (val upsertResult = pushDirtyItems(backupId, deviceId, dirtyItems, encryptedPayloads)) {
                is UpsertOutcome.Success -> {
                    succeededKeys.addAll(upsertResult.succeeded)
                    conflictedKeys.addAll(upsertResult.conflicted)
                }
                is UpsertOutcome.Error -> return PushSyncResult.Error(upsertResult.exception)
            }
        }

        for ((key, _) in pendingDeletes) {
            when (val deleteResult = backupRepository.deleteItem(backupId, key)) {
                is PeraResult.Success -> {
                    syncStateRepository.removeItem(backupId, key)
                    deletedKeys.add(key)
                }
                is PeraResult.Error -> return PushSyncResult.Error(deleteResult.exception)
            }
        }

        return PushSyncResult.Pushed(
            succeededKeys = succeededKeys,
            conflictedKeys = conflictedKeys,
            deletedKeys = deletedKeys
        )
    }

    private suspend fun pushDirtyItems(
        backupId: BackupId,
        deviceId: DeviceId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        encryptedPayloads: Map<BackupItemKey, String>
    ): UpsertOutcome {
        val batchInputs = dirtyItems.mapNotNull { (key, itemState) ->
            val payload = encryptedPayloads[key] ?: return@mapNotNull null
            BatchUpsertInput(
                key = key,
                expectedVersion = itemState.baseVersion,
                status = itemState.status,
                payload = payload
            )
        }

        if (batchInputs.isEmpty()) return UpsertOutcome.Success(emptyList(), emptyList())

        return when (val result = backupRepository.batchUpsertItems(backupId, deviceId, batchInputs)) {
            is PeraResult.Success -> {
                val succeeded = mutableListOf<BackupItemKey>()
                val conflicted = mutableListOf<BackupItemKey>()

                for (itemResult in result.data) {
                    val existingItem = dirtyItems[itemResult.key] ?: continue
                    when (val upsertResult = itemResult.result) {
                        is UpsertItemResult.Success -> {
                            val updated = syncItemStateMapper.mapFromPushSuccess(existingItem, upsertResult.newVersion)
                            syncStateRepository.updateItemState(backupId, itemResult.key, updated)
                            succeeded.add(itemResult.key)
                        }
                        is UpsertItemResult.Conflict -> {
                            conflicted.add(itemResult.key)
                        }
                    }
                }

                UpsertOutcome.Success(succeeded, conflicted)
            }
            is PeraResult.Error -> UpsertOutcome.Error(result.exception)
        }
    }

    private sealed interface UpsertOutcome {
        data class Success(
            val succeeded: List<BackupItemKey>,
            val conflicted: List<BackupItemKey>
        ) : UpsertOutcome

        data class Error(val exception: Exception) : UpsertOutcome
    }
}
