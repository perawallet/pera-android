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

import android.util.Base64
import com.algorand.android.ui.backup.sync.security.BackupSyncPayload
import com.algorand.android.ui.backup.sync.security.BackupSyncQrPayloadCipher
import javax.inject.Inject

internal class DefaultGetEncryptedBackupSyncQrPayload @Inject constructor(
    private val cipher: BackupSyncQrPayloadCipher
) : GetEncryptedBackupSyncQrPayload {

    override fun invoke(pin: String, payload: BackupSyncPayload): String {
        val encrypted = cipher.encrypt(pin, payload)
        val encoded = Base64.encodeToString(encrypted, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        return BACKUP_SYNC_QR_WIRE_PREFIX + encoded
    }
}
