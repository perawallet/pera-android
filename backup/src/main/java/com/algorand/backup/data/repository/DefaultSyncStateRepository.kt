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

package com.algorand.backup.data.repository

import com.algorand.backup.data.mapper.SyncStateCacheMapper
import com.algorand.backup.data.model.SyncStateCacheModel
import com.algorand.backup.domain.model.BackupGlobalHash
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupSyncResult
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.model.SyncState
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.cache.PersistentCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultSyncStateRepository(
    private val persistentCache: PersistentCache<SyncStateCacheModel>,
    private val mapper: SyncStateCacheMapper
) : SyncStateRepository {

    private val mutex = Mutex()

    override suspend fun getSyncState(backupId: BackupId): SyncState? {
        return persistentCache.get()
            ?.takeIf { it.backupId == backupId.value }
            ?.let { mapper.toDomainModel(it) }
    }

    override suspend fun saveSyncState(state: SyncState) {
        persistentCache.put(mapper.toCacheModel(state))
    }

    override suspend fun deleteSyncState(backupId: BackupId) {
        val current = persistentCache.get() ?: return
        if (current.backupId == backupId.value) {
            persistentCache.clear()
        }
    }

    override suspend fun updateItemState(backupId: BackupId, key: BackupItemKey, itemState: SyncItemState) {
        mutex.withLock {
            val current = persistentCache.get()
                ?.takeIf { it.backupId == backupId.value } ?: return
            val updatedItems = current.items.toMutableMap().apply {
                put(key.value, mapper.toItemCacheModel(itemState))
            }
            persistentCache.put(current.copy(items = updatedItems))
        }
    }

    override suspend fun updateGlobalPointers(
        backupId: BackupId,
        lastKnownBackupHash: BackupGlobalHash,
        lastSyncedSeq: Long
    ) {
        mutex.withLock {
            val current = persistentCache.get()
                ?.takeIf { it.backupId == backupId.value } ?: return
            persistentCache.put(
                current.copy(
                    lastKnownBackupHash = lastKnownBackupHash.value,
                    lastSyncedSeq = lastSyncedSeq
                )
            )
        }
    }

    override suspend fun recordLatestSync(
        backupId: BackupId,
        timestampMillis: Long,
        result: BackupSyncResult
    ) {
        mutex.withLock {
            val current = persistentCache.get()
                ?.takeIf { it.backupId == backupId.value } ?: return
            persistentCache.put(
                current.copy(
                    lastSyncedAt = timestampMillis,
                    lastSyncResult = result.name
                )
            )
        }
    }

    override suspend fun getDirtyItems(backupId: BackupId): Map<BackupItemKey, SyncItemState> {
        val current = persistentCache.get()
            ?.takeIf { it.backupId == backupId.value } ?: return emptyMap()
        return current.items
            .filter { it.value.isDirty }
            .map { (key, state) -> BackupItemKey(key) to mapper.toItemDomainModel(state) }
            .toMap()
    }

    override suspend fun getPendingDeletes(backupId: BackupId): Map<BackupItemKey, SyncItemState> {
        val current = persistentCache.get()
            ?.takeIf { it.backupId == backupId.value }
            ?: return emptyMap()
        return current.items
            .filter { it.value.pendingDelete }
            .map { (key, state) -> BackupItemKey(key) to mapper.toItemDomainModel(state) }
            .toMap()
    }

    override suspend fun removeItem(backupId: BackupId, key: BackupItemKey) {
        mutex.withLock {
            val current = persistentCache.get()?.takeIf { it.backupId == backupId.value } ?: return
            val updatedItems = current.items.toMutableMap().apply {
                remove(key.value)
            }
            persistentCache.put(current.copy(items = updatedItems))
        }
    }
}
