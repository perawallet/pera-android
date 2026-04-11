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

import com.algorand.backup.data.api.model.BackupManifestItemResponse
import com.algorand.backup.data.api.model.BackupManifestResponse
import com.algorand.backup.domain.model.BackupGlobalHash
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.ItemHash
import com.algorand.backup.domain.model.BackupManifest
import com.algorand.backup.domain.model.BackupManifestItem
import javax.inject.Inject

internal class ManifestResponseMapper @Inject constructor() {

    fun toDomainModel(response: BackupManifestResponse): BackupManifest? {
        return BackupManifest(
            backupId = BackupId(response.backupId ?: return null),
            backupGlobalHash = BackupGlobalHash(response.backupGlobalHash ?: return null),
            globalVersion = response.globalVersion ?: return null,
            lastSeq = response.lastSeq ?: return null,
            items = response.items?.mapNotNull { (key, item) ->
                toManifestItem(item)?.let { BackupItemKey(key) to it }
            }?.toMap() ?: emptyMap()
        )
    }

    private fun toManifestItem(response: BackupManifestItemResponse): BackupManifestItem? {
        return BackupManifestItem(
            type = BackupItemType.valueOf(response.type ?: return null),
            version = response.version ?: return null,
            status = BackupItemStatus.valueOf(response.status ?: return null),
            hash = ItemHash(response.hash ?: return null),
            lastSeq = response.lastSeq ?: return null
        )
    }
}
