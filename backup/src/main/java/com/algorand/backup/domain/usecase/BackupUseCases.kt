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
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.PullSyncResult
import com.algorand.backup.domain.model.PushSyncResult

internal interface PullBackupSync {
    suspend operator fun invoke(backupId: BackupId): PullSyncResult
}

internal interface PushBackupSync {
    suspend operator fun invoke(
        backupId: BackupId,
        deviceId: DeviceId,
        encryptedPayloads: Map<BackupItemKey, String>
    ): PushSyncResult
}

internal interface DeleteBackupItem {
    suspend operator fun invoke(backupId: BackupId, key: BackupItemKey, deleteFromServer: Boolean)
}
