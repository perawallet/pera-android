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
import com.algorand.backup.data.model.BackupSessionCacheData
import com.algorand.backup.domain.model.AesGcmEncryptionResult
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.backup.domain.repository.BackupSessionRepository
import com.algorand.backup.domain.security.PeraAndroidKeyStore
import com.algorand.backup.domain.security.PeraCipher
import com.algorand.backup.domain.security.safeDestroy
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.PersistentCache

internal class DefaultBackupSessionRepository(
    private val persistentCache: PersistentCache<BackupSessionCacheData>,
    private val peraAndroidKeyStore: PeraAndroidKeyStore,
    private val peraCipher: PeraCipher
) : BackupSessionRepository {

    override fun storeSession(
        backupId: BackupId,
        deviceId: DeviceId,
        authPrivateKey: SensitiveBytes
    ): PeraResult<Unit> {
        return try {
            clearSession()
            ensureWrappingKey()

            val wrappingKey = peraAndroidKeyStore.getSecretKey(WRAPPING_KEY_ALIAS)
                ?: return PeraResult.Error(IllegalStateException("Failed to create wrapping key"))

            try {
                val encryptionResult = peraCipher.encrypt(wrappingKey, authPrivateKey.reveal())
                persistentCache.put(toCacheData(backupId, deviceId, encryptionResult))
            } finally {
                wrappingKey.safeDestroy()
            }
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override fun <T : Any> usePrivateKey(block: (SensitiveBytes) -> T): PeraResult<T> {
        val cached = persistentCache.get()
        val wrappingKey = peraAndroidKeyStore.getSecretKey(WRAPPING_KEY_ALIAS)
        if (cached == null || wrappingKey == null) {
            return PeraResult.Error(IllegalStateException("Backup session or wrapping key not found"))
        }
        return try {
            val wrapped = Base64.decode(cached.wrappedPrivateKey, Base64.NO_WRAP)
            val iv = Base64.decode(cached.wrappingIv, Base64.NO_WRAP)
            SensitiveBytes(peraCipher.decrypt(wrappingKey, wrapped, iv)).use { key ->
                PeraResult.Success(block(key))
            }
        } catch (e: Exception) {
            PeraResult.Error(e)
        } finally {
            wrappingKey.safeDestroy()
        }
    }

    override fun getBackupId(): BackupId? = persistentCache.get()?.backupId?.let { BackupId(it) }

    override fun getDeviceId(): DeviceId? = persistentCache.get()?.deviceId?.let { DeviceId(it) }

    override fun hasSession(): Boolean = persistentCache.get() != null

    override fun clearSession() {
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
        deviceId: DeviceId,
        encryptionResult: AesGcmEncryptionResult
    ): BackupSessionCacheData {
        return BackupSessionCacheData(
            backupId = backupId.value,
            deviceId = deviceId.value,
            wrappedPrivateKey = Base64.encodeToString(encryptionResult.ciphertext, Base64.NO_WRAP),
            wrappingIv = Base64.encodeToString(encryptionResult.iv, Base64.NO_WRAP)
        )
    }

    private companion object {
        const val WRAPPING_KEY_ALIAS = "pera_backup_session_wrapping_key"
        const val AES_KEY_SIZE_BITS = 256
    }
}
