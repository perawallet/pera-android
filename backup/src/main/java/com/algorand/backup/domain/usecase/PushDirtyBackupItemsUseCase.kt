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
import com.algorand.backup.domain.model.BackupBatchUpsertInput
import com.algorand.backup.domain.model.BackupBatchUpsertItemResult
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupUpsertItemResult
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PushedDirtyBackupItems
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class PushDirtyBackupItemsUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper,
    private val errorLogger: PeraErrorLogger
) : PushDirtyBackupItems {

    override suspend fun invoke(
        backupId: BackupId,
        deviceId: DeviceId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PeraResult<PushedDirtyBackupItems> {
        val batchInputs = dirtyItems.mapNotNull { (key, itemState) ->
            val payload = encryptedPayloads[key]
            if (payload == null) {
                errorLogger.logError(IllegalStateException("Missing encrypted payload for dirty item: $key"))
                return@mapNotNull null
            }
            BackupBatchUpsertInput(
                key = key,
                type = itemState.type,
                expectedVersion = itemState.baseVersion,
                status = itemState.status,
                payload = payload
            )
        }

        if (batchInputs.isEmpty()) return PeraResult.Success(PushedDirtyBackupItems(emptyList(), emptyList()))

        return when (val result = backupRepository.batchUpsertItems(backupId, deviceId, batchInputs)) {
            is PeraResult.Success -> handleUpsertSuccess(backupId, dirtyItems, result.data)
            is PeraResult.Error -> handleUpsertError(backupId, dirtyItems, result)
        }
    }

    private suspend fun handleUpsertSuccess(
        backupId: BackupId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        results: List<BackupBatchUpsertItemResult>
    ): PeraResult.Success<PushedDirtyBackupItems> {
        val succeeded = mutableListOf<BackupItemKey>()
        val conflicted = mutableListOf<BackupItemKey>()
        var maxSeq = 0L

        for (itemResult in results) {
            val existingItem = dirtyItems[itemResult.key] ?: continue
            when (val upsertResult = itemResult.result) {
                is BackupUpsertItemResult.Success -> {
                    val updated = syncItemStateMapper.mapFromPushSuccess(existingItem, upsertResult.newVersion)
                    syncStateRepository.updateItemState(backupId, itemResult.key, updated)
                    succeeded.add(itemResult.key)
                    maxSeq = maxOf(maxSeq, upsertResult.seq)
                }
                is BackupUpsertItemResult.Conflict -> {
                    conflicted.add(itemResult.key)
                }
            }
        }

        return PeraResult.Success(PushedDirtyBackupItems(succeeded, conflicted, maxSeq))
    }

    private suspend fun handleUpsertError(
        backupId: BackupId,
        dirtyItems: Map<BackupItemKey, SyncItemState>,
        error: PeraResult.Error
    ): PeraResult<PushedDirtyBackupItems> {
        if (error.code == HTTP_NOT_FOUND) {
            for ((key, itemState) in dirtyItems) {
                val ignoredItem = syncItemStateMapper.mapForLocalOnlyDelete(itemState)
                syncStateRepository.updateItemState(backupId, key, ignoredItem)
            }
            return PeraResult.Success(PushedDirtyBackupItems(emptyList(), emptyList()))
        }
        return PeraResult.Error(error.exception)
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
