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

package com.algorand.backup.domain.mapper

import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.ManifestItem
import com.algorand.backup.domain.model.SyncItemState
import javax.inject.Inject

internal class SyncItemStateMapper @Inject constructor() {

    fun mapFromManifestItem(manifestItem: ManifestItem): SyncItemState {
        return SyncItemState(
            type = manifestItem.type,
            knownVersion = manifestItem.version,
            baseVersion = manifestItem.version,
            isDirty = false,
            status = BackupItemStatus.ACTIVE,
            lastRemoteHash = null,
            pendingDelete = false
        )
    }

    fun mapFromUpsertDelta(delta: DeltaEntry, existingItem: SyncItemState?): SyncItemState {
        return SyncItemState(
            type = delta.type,
            knownVersion = delta.version,
            baseVersion = if (existingItem?.isDirty == true) existingItem.baseVersion else delta.version,
            isDirty = existingItem?.isDirty ?: false,
            status = BackupItemStatus.valueOf(delta.status.name),
            lastRemoteHash = delta.hash,
            pendingDelete = existingItem?.pendingDelete ?: false
        )
    }

    fun mapFromPushSuccess(existingItem: SyncItemState, newVersion: Int): SyncItemState {
        return existingItem.copy(
            knownVersion = newVersion,
            baseVersion = newVersion,
            isDirty = false
        )
    }

    fun mapForGlobalDelete(existingItem: SyncItemState): SyncItemState {
        return existingItem.copy(
            isDirty = false,
            pendingDelete = true
        )
    }

    fun mapForLocalOnlyDelete(existingItem: SyncItemState): SyncItemState {
        return existingItem.copy(
            baseVersion = existingItem.knownVersion,
            isDirty = false,
            status = BackupItemStatus.IGNORED,
            pendingDelete = false
        )
    }

    fun mapForReactivate(existingItem: SyncItemState): SyncItemState {
        return existingItem.copy(
            baseVersion = existingItem.knownVersion,
            isDirty = true,
            status = BackupItemStatus.ACTIVE,
            pendingDelete = false
        )
    }

    fun mapFromDeleteDelta(delta: DeltaEntry, existingItem: SyncItemState): SyncItemState {
        return SyncItemState(
            type = existingItem.type,
            knownVersion = delta.version,
            baseVersion = delta.version,
            isDirty = false,
            status = BackupItemStatus.IGNORED,
            lastRemoteHash = null,
            pendingDelete = false
        )
    }
}
