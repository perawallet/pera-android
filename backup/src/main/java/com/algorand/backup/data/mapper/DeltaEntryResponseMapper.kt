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

import com.algorand.backup.data.api.model.BackupDeltaEntryResponse
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.DeltaOperation
import com.algorand.backup.domain.model.ItemHash
import javax.inject.Inject

internal class DeltaEntryResponseMapper @Inject constructor() {

    fun toDomainModel(response: BackupDeltaEntryResponse): DeltaEntry? {
        return DeltaEntry(
            seq = response.seq ?: return null,
            key = BackupItemKey(response.key ?: return null),
            type = BackupItemType.valueOf(response.type ?: return null),
            version = response.version ?: return null,
            status = BackupItemStatus.valueOf(response.status ?: return null),
            operation = DeltaOperation.valueOf(response.operation ?: return null),
            hash = response.hash?.let { ItemHash(it) }
        )
    }
}
