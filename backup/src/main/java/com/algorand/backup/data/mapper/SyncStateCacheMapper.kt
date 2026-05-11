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

package com.algorand.backup.data.mapper

import com.algorand.backup.data.model.SyncItemStateCacheModel
import com.algorand.backup.data.model.SyncStateCacheModel
import com.algorand.backup.domain.model.BackupGlobalHash
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.BackupSyncResult
import com.algorand.backup.domain.model.ItemHash
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.model.SyncState
import javax.inject.Inject

internal class SyncStateCacheMapper @Inject constructor() {

    fun toCacheModel(domain: SyncState): SyncStateCacheModel {
        return SyncStateCacheModel(
            backupId = domain.backupId.value,
            lastKnownBackupHash = domain.lastKnownBackupHash?.value,
            lastSyncedSeq = domain.lastSyncedSeq,
            items = domain.items.map { (key, state) ->
                key.value to toItemCacheModel(state)
            }.toMap(),
            lastSyncedAt = domain.lastSyncedAt,
            lastSyncResult = domain.lastSyncResult?.name
        )
    }

    fun toDomainModel(cache: SyncStateCacheModel): SyncState {
        return SyncState(
            backupId = BackupId(cache.backupId),
            lastKnownBackupHash = cache.lastKnownBackupHash?.let { BackupGlobalHash(it) },
            lastSyncedSeq = cache.lastSyncedSeq,
            items = cache.items.map { (key, state) ->
                BackupItemKey(key) to toItemDomainModel(state)
            }.toMap(),
            lastSyncedAt = cache.lastSyncedAt,
            lastSyncResult = cache.lastSyncResult?.let { runCatching { BackupSyncResult.valueOf(it) }.getOrNull() }
        )
    }

    fun toItemCacheModel(domain: SyncItemState): SyncItemStateCacheModel {
        return SyncItemStateCacheModel(
            type = domain.type.name,
            knownVersion = domain.knownVersion,
            baseVersion = domain.baseVersion,
            isDirty = domain.isDirty,
            status = domain.status.name,
            lastRemoteHash = domain.lastRemoteHash?.value,
            pendingDelete = domain.pendingDelete
        )
    }

    fun toItemDomainModel(cache: SyncItemStateCacheModel): SyncItemState {
        return SyncItemState(
            type = BackupItemType.valueOf(cache.type),
            knownVersion = cache.knownVersion,
            baseVersion = cache.baseVersion,
            isDirty = cache.isDirty,
            status = BackupItemStatus.valueOf(cache.status),
            lastRemoteHash = cache.lastRemoteHash?.let { ItemHash(it) },
            pendingDelete = cache.pendingDelete
        )
    }
}
