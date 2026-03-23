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

package com.algorand.backup.domain.repository

import com.algorand.backup.domain.model.BackupGlobalHash
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.model.SyncState

internal interface SyncStateRepository {

    suspend fun getSyncState(backupId: BackupId): SyncState?

    suspend fun saveSyncState(state: SyncState)

    suspend fun deleteSyncState(backupId: BackupId)

    suspend fun updateItemState(backupId: BackupId, key: BackupItemKey, itemState: SyncItemState)

    suspend fun updateGlobalPointers(
        backupId: BackupId,
        lastKnownBackupHash: BackupGlobalHash,
        lastSyncedSeq: Long
    )

    suspend fun getDirtyItems(backupId: BackupId): Map<BackupItemKey, SyncItemState>

    suspend fun getPendingDeletes(backupId: BackupId): Map<BackupItemKey, SyncItemState>

    suspend fun removeItem(backupId: BackupId, key: BackupItemKey)
}
