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
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.repository.SyncStateRepository
import javax.inject.Inject

internal class DefaultBackupSyncStateUpdater @Inject constructor(
    private val getBackupId: GetBackupId,
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper
) : BackupSyncStateUpdater {

    override suspend fun markDirty(keys: Set<BackupItemKey>, type: BackupItemType) {
        val backupId = getBackupId() ?: return
        keys.forEach { key -> markItemDirty(backupId, key, type) }
    }

    override suspend fun markPendingDelete(keys: Set<BackupItemKey>) {
        val backupId = getBackupId() ?: return
        keys.forEach { key -> markItemPendingDelete(backupId, key) }
    }

    override suspend fun markIgnored(keys: Set<BackupItemKey>) {
        val backupId = getBackupId() ?: return
        keys.forEach { key -> markItemIgnored(backupId, key) }
    }

    private suspend fun markItemPendingDelete(backupId: BackupId, key: BackupItemKey) {
        val syncState = syncStateRepository.getSyncState(backupId)
        val existingItem = syncState?.items?.get(key) ?: return
        if (existingItem.status == BackupItemStatus.IGNORED) return
        val deletedItem = existingItem.copy(pendingDelete = true)
        syncStateRepository.updateItemState(backupId, key, deletedItem)
    }

    private suspend fun markItemIgnored(backupId: BackupId, key: BackupItemKey) {
        val syncState = syncStateRepository.getSyncState(backupId)
        val existingItem = syncState?.items?.get(key) ?: return
        val ignoredItem = syncItemStateMapper.mapForLocalOnlyDelete(existingItem)
        syncStateRepository.updateItemState(backupId, key, ignoredItem)
    }

    private suspend fun markItemDirty(backupId: BackupId, key: BackupItemKey, type: BackupItemType) {
        val syncState = syncStateRepository.getSyncState(backupId)
        val existingItem = syncState?.items?.get(key)

        if (existingItem != null) {
            markExistingItemDirty(backupId, key, existingItem)
        } else {
            createNewDirtyItem(backupId, key, type)
        }
    }

    private suspend fun markExistingItemDirty(backupId: BackupId, key: BackupItemKey, existingItem: SyncItemState) {
        if (existingItem.pendingDelete) return
        val dirtyItem = existingItem.copy(
            baseVersion = existingItem.knownVersion,
            isDirty = true,
            status = BackupItemStatus.ACTIVE
        )
        syncStateRepository.updateItemState(backupId, key, dirtyItem)
    }

    private suspend fun createNewDirtyItem(backupId: BackupId, key: BackupItemKey, type: BackupItemType) {
        val newItem = syncItemStateMapper.mapNewDirtyItem(type)
        syncStateRepository.updateItemState(backupId, key, newItem)
    }
}
