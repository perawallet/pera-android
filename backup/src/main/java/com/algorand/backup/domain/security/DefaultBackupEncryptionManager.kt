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

package com.algorand.backup.domain.security

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.wallet.foundation.PeraResult
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

internal class DefaultBackupEncryptionManager @Inject constructor(
    private val peraAndroidKeyStore: PeraAndroidKeyStore,
    private val peraCipher: PeraCipher
) : BackupEncryptionManager {

    override fun importKey(encryptionKey: SensitiveBytes): PeraResult<Unit> {
        return try {
            val secretKey = SecretKeySpec(encryptionKey.reveal(), KeyProperties.KEY_ALGORITHM_AES)
            val protection = KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            try {
                peraAndroidKeyStore.setSecretKeyEntry(KEYSTORE_ALIAS, secretKey, protection)
            } finally {
                secretKey.safeDestroy()
            }
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override fun encrypt(plaintext: ByteArray, itemKey: String): PeraResult<ByteArray> {
        return try {
            val key = peraAndroidKeyStore.getSecretKey(KEYSTORE_ALIAS)
                ?: return PeraResult.Error(IllegalStateException("Backup encryption key not found in keystore"))
            val aad = itemKey.toByteArray(Charsets.UTF_8)
            val result = peraCipher.encrypt(key, plaintext, aad)
            PeraResult.Success(result.iv + result.ciphertext)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override fun decrypt(ciphertext: ByteArray, itemKey: String): PeraResult<ByteArray> {
        return try {
            if (ciphertext.size <= GCM_IV_LENGTH) {
                return PeraResult.Error(IllegalArgumentException("Ciphertext too short"))
            }
            val key = peraAndroidKeyStore.getSecretKey(KEYSTORE_ALIAS)
                ?: return PeraResult.Error(IllegalStateException("Backup encryption key not found in keystore"))
            val iv = ciphertext.copyOfRange(0, GCM_IV_LENGTH)
            val encrypted = ciphertext.copyOfRange(GCM_IV_LENGTH, ciphertext.size)
            val aad = itemKey.toByteArray(Charsets.UTF_8)
            PeraResult.Success(peraCipher.decrypt(key, encrypted, iv, aad))
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override fun deleteKey() {
        peraAndroidKeyStore.deleteEntry(KEYSTORE_ALIAS)
    }

    private companion object {
        const val KEYSTORE_ALIAS = "pera_backup_encryption_key"
        const val GCM_IV_LENGTH = 12
    }
}
