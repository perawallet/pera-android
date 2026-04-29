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

package com.algorand.backup.data.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.algorand.backup.data.model.BackupAuthCredentialsCacheData
import com.algorand.backup.domain.model.AesGcmEncryptionResult
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.backup.domain.repository.BackupAuthCredentialsRepository
import com.algorand.backup.domain.security.PeraAndroidKeyStore
import com.algorand.backup.domain.security.PeraCipher
import com.algorand.backup.domain.security.safeDestroy
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.PersistentCache

internal class DefaultBackupAuthCredentialsRepository(
    private val persistentCache: PersistentCache<BackupAuthCredentialsCacheData>,
    private val peraAndroidKeyStore: PeraAndroidKeyStore,
    private val peraCipher: PeraCipher
) : BackupAuthCredentialsRepository {

    override fun storeCredentials(
        backupId: BackupId,
        mnemonic: SensitiveBytes,
        salt: ByteArray
    ): PeraResult<Unit> {
        return try {
            clearCredentials()
            ensureWrappingKey()

            val wrappingKey = peraAndroidKeyStore.getSecretKey(WRAPPING_KEY_ALIAS)
                ?: return PeraResult.Error(IllegalStateException("Failed to create wrapping key"))

            try {
                val mnemonicEncryption = peraCipher.encrypt(wrappingKey, mnemonic.reveal())
                val saltEncryption = peraCipher.encrypt(wrappingKey, salt)
                persistentCache.put(toCacheData(backupId, mnemonicEncryption, saltEncryption))
            } finally {
                wrappingKey.safeDestroy()
            }
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override fun <T : Any> useCredentials(
        block: (backupId: BackupId, mnemonic: SensitiveBytes, salt: ByteArray) -> T
    ): PeraResult<T> {
        val cached = persistentCache.get()
        val wrappingKey = peraAndroidKeyStore.getSecretKey(WRAPPING_KEY_ALIAS)
        if (cached == null || wrappingKey == null) {
            return PeraResult.Error(IllegalStateException("Backup auth credentials or wrapping key not found"))
        }
        return try {
            val mnemonicCiphertext = Base64.decode(cached.wrappedMnemonic, Base64.NO_WRAP)
            val mnemonicIv = Base64.decode(cached.mnemonicIv, Base64.NO_WRAP)
            val saltCiphertext = Base64.decode(cached.wrappedSalt, Base64.NO_WRAP)
            val saltIv = Base64.decode(cached.saltIv, Base64.NO_WRAP)

            val saltBytes = peraCipher.decrypt(wrappingKey, saltCiphertext, saltIv)
            SensitiveBytes(peraCipher.decrypt(wrappingKey, mnemonicCiphertext, mnemonicIv)).use { mnemonic ->
                PeraResult.Success(block(BackupId(cached.backupId), mnemonic, saltBytes))
            }
        } catch (e: Exception) {
            PeraResult.Error(e)
        } finally {
            wrappingKey.safeDestroy()
        }
    }

    override fun hasCredentials(): Boolean = persistentCache.get() != null

    override fun clearCredentials() {
        persistentCache.clear()
        peraAndroidKeyStore.deleteEntry(WRAPPING_KEY_ALIAS)
    }

    private fun ensureWrappingKey() {
        if (peraAndroidKeyStore.containsAlias(WRAPPING_KEY_ALIAS)) return

        val spec = KeyGenParameterSpec
            .Builder(WRAPPING_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(AES_KEY_SIZE_BITS)
            .build()
        peraAndroidKeyStore.generateSecretKey(spec)
    }

    private fun toCacheData(
        backupId: BackupId,
        mnemonicEncryption: AesGcmEncryptionResult,
        saltEncryption: AesGcmEncryptionResult
    ): BackupAuthCredentialsCacheData {
        return BackupAuthCredentialsCacheData(
            backupId = backupId.value,
            wrappedMnemonic = Base64.encodeToString(mnemonicEncryption.ciphertext, Base64.NO_WRAP),
            mnemonicIv = Base64.encodeToString(mnemonicEncryption.iv, Base64.NO_WRAP),
            wrappedSalt = Base64.encodeToString(saltEncryption.ciphertext, Base64.NO_WRAP),
            saltIv = Base64.encodeToString(saltEncryption.iv, Base64.NO_WRAP)
        )
    }

    private companion object {
        const val WRAPPING_KEY_ALIAS = "pera_backup_auth_credentials_wrapping_key"
        const val AES_KEY_SIZE_BITS = 256
    }
}
