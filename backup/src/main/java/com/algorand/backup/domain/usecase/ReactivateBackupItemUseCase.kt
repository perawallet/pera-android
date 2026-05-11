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
import com.algorand.backup.domain.repository.SyncStateRepository
import javax.inject.Inject

internal class ReactivateBackupItemUseCase @Inject constructor(
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper
) : ReactivateBackupItem {

    override suspend fun invoke(backupId: BackupId, key: BackupItemKey) {
        val syncState = syncStateRepository.getSyncState(backupId) ?: return
        val existingItem = syncState.items[key] ?: return
        if (existingItem.status != BackupItemStatus.IGNORED) return

        val reactivatedItem = syncItemStateMapper.mapForReactivate(existingItem)
        syncStateRepository.updateItemState(backupId, key, reactivatedItem)
    }
}
