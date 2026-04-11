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
import com.algorand.backup.domain.model.DeletedBackupItems
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class DeletePendingBackupItemsUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val syncStateRepository: SyncStateRepository,
    private val errorLogger: PeraErrorLogger
) : DeletePendingBackupItems {

    override suspend fun invoke(
        backupId: BackupId,
        pendingDeletes: Map<BackupItemKey, SyncItemState>
    ): PeraResult<DeletedBackupItems> {
        val deletedKeys = mutableListOf<BackupItemKey>()
        var maxSeq = 0L

        pendingDeletes.forEach { (key, _) ->
            when (val deleteResult = backupRepository.deleteItem(backupId, key)) {
                is PeraResult.Success -> {
                    syncStateRepository.removeItem(backupId, key)
                    deletedKeys.add(key)
                    if (deleteResult.data > 0) maxSeq = maxOf(maxSeq, deleteResult.data)
                }
                is PeraResult.Error -> {
                    if (deleteResult.code == HTTP_NOT_FOUND) {
                        syncStateRepository.removeItem(backupId, key)
                        deletedKeys.add(key)
                    } else {
                        errorLogger.logError(deleteResult.exception)
                    }
                }
            }
        }

        return PeraResult.Success(DeletedBackupItems(deletedKeys, maxSeq))
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
    }
}
