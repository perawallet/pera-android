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

package com.algorand.android.ui.backup.sync.usecase

import com.algorand.android.ui.backup.sync.security.BackupSyncPayload

internal const val BACKUP_SYNC_QR_WIRE_PREFIX = "pera-sync://v1/"

fun interface GetEncryptedBackupSyncQrPayload {
    operator fun invoke(pin: String, payload: BackupSyncPayload): String
}

fun interface GetDecryptedBackupSyncQrPayload {
    operator fun invoke(pin: String, wirePayload: String): Result<BackupSyncPayload>
}

fun interface IsBackupSyncQrPayload {
    operator fun invoke(value: String): Boolean
}
