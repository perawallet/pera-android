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

import android.util.Base64
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class EncryptBackupPayloadsUseCase @Inject constructor(
    private val encryptionManager: BackupEncryptionManager
) : EncryptBackupPayloads {

    override suspend fun invoke(payloads: Map<BackupItemKey, ByteArray>): PeraResult<Map<BackupItemKey, String>> {
        val result = mutableMapOf<BackupItemKey, String>()
        payloads.forEach { (key, plaintext) ->
            when (val encryptResult = encryptionManager.encrypt(plaintext, key.value)) {
                is PeraResult.Success -> {
                    result[key] = Base64.encodeToString(encryptResult.data, Base64.NO_WRAP)
                }
                is PeraResult.Error -> return PeraResult.Error(encryptResult.exception)
            }
        }
        return PeraResult.Success(result)
    }
}
