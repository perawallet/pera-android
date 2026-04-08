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

import com.algorand.backup.data.api.model.BackupBatchUpsertItemRequest
import com.algorand.backup.data.api.model.BackupUpsertItemRequest
import com.algorand.backup.domain.model.BackupBatchUpsertInput
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.DeviceId
import javax.inject.Inject

internal class BackupUpsertItemRequestMapper @Inject constructor() {

    fun toRequest(
        type: BackupItemType,
        expectedVersion: Int,
        status: BackupItemStatus,
        deviceId: DeviceId,
        payload: String
    ): BackupUpsertItemRequest {
        return BackupUpsertItemRequest(
            type = type.name,
            expectedVersion = expectedVersion,
            status = status.name,
            deviceId = deviceId.value,
            payload = payload
        )
    }

    fun toRequest(input: BackupBatchUpsertInput): BackupBatchUpsertItemRequest {
        return BackupBatchUpsertItemRequest(
            key = input.key.value,
            type = input.type.name,
            expectedVersion = input.expectedVersion,
            status = input.status.name,
            payload = input.payload
        )
    }
}
