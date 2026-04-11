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
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class DecryptBackupPayloadsUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val encryptionManager: BackupEncryptionManager,
    private val errorLogger: PeraErrorLogger
) : DecryptBackupPayloads {

    override suspend fun invoke(
        backupId: BackupId,
        keys: List<BackupItemKey>
    ): PeraResult<Map<BackupItemKey, ByteArray>> {
        if (keys.isEmpty()) return PeraResult.Success(emptyMap())

        return backupRepository.batchReadItems(backupId, keys).map { encryptedPayloads ->
            val decrypted = mutableMapOf<BackupItemKey, ByteArray>()
            encryptedPayloads.forEach { (key, base64Payload) ->
                val encryptedBytes = Base64.decode(base64Payload, Base64.NO_WRAP)
                when (val decryptResult = encryptionManager.decrypt(encryptedBytes, key.value)) {
                    is PeraResult.Success -> decrypted[key] = decryptResult.data
                    is PeraResult.Error -> errorLogger.logError(decryptResult.exception)
                }
            }
            decrypted
        }
    }
}
