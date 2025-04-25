/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.encryption.domain.usecase

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.algorand.wallet.foundation.PeraResult
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

internal class AndroidEncryptionManagerImpl @Inject constructor(
    private val getStrongBoxUsedCheck: GetStrongBoxUsedCheck,
    private val saveStrongBoxUsedCheck: SaveStrongBoxUsedCheck,
) : AndroidEncryptionManager {

    override suspend fun initializeEncryptionManager() {
        generateKeyIfNeeded()
    }

    /**
     * Checks if the current key should be migrated to StrongBox
     * @return true if migration is recommended
     */
    override suspend fun shouldMigrateToStrongBox(): Boolean {
        if (getStrongBoxUsedCheck.invoke()) {
            return false
        }

        try {
            initializeStrongBoxEncryption()
            return true
        } catch (e: Exception) {
            // StrongBox still not available
            return false
        } finally {
            try {
                // Clean up the test key if it was created
                cleanUpStrongBoxTestAlias()
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up test key", e)
            }
        }
    }

    private fun initializeStrongBoxEncryption() {
        val keyGenerator = getKeyGenerator()
        val testBuilder = getKeyGenParameterSpecBuilder(STRONG_BOX_TEST_ALIAS)
            .setIsStrongBoxBacked(true)
        keyGenerator.init(testBuilder.build())
    }

    private fun cleanUpStrongBoxTestAlias() {
        val keyStore = getKeyStore()
        if (keyStore.containsAlias(STRONG_BOX_TEST_ALIAS)) {
            keyStore.deleteEntry(STRONG_BOX_TEST_ALIAS)
        }
    }

    /**
     * Migrates encryption keys to StrongBox
     * @return true if migration was successful
     */
    override suspend fun migrateToStrongBox(): PeraResult<Boolean> {
        if (!shouldMigrateToStrongBox()) {
            return PeraResult.Success(false)
        }

        try {
            val keyStore = getKeyStore()
            val originalKeyExists = keyStore.containsAlias(KEY_ALIAS)
            if (!originalKeyExists) {
                Log.d(TAG, "No key to migrate to StrongBox")
                return PeraResult.Success(false)
            }

            val keyGenerator = getKeyGenerator()
            createKey(keyGenerator, STRONG_BOX_ALIAS, useStrongBox = true)

            saveStrongBoxUsedCheck.invoke(true)

            // migrate all secret keys in db tables
            // not completely implemented yet so return false

            return PeraResult.Success(false)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to migrate to StrongBox", e)
            return PeraResult.Error(e)
        }
    }

    private fun getKeyStore(): KeyStore {
        return KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    private fun getKeyGenerator(): KeyGenerator {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    }

    private suspend fun generateKeyIfNeeded() {
        val keyStore = getKeyStore()

        if (!keyStore.containsAlias(KEY_ALIAS) && !keyStore.containsAlias(STRONG_BOX_ALIAS)) {
            val keyGenerator = getKeyGenerator()

            try {
                // Try to create StrongBox-backed key first
                createKey(keyGenerator, STRONG_BOX_ALIAS, useStrongBox = true)
                saveStrongBoxUsedCheck.invoke(true)
                Log.d(TAG, "StrongBox key generated successfully")
            } catch (e: java.security.ProviderException) {
                // Fall back to software-backed key
                Log.d(TAG, "StrongBox not available, falling back to software-backed key", e)
                createKey(keyGenerator, KEY_ALIAS, useStrongBox = false)
                saveStrongBoxUsedCheck.invoke(false)
                Log.d(TAG, "Software-backed key generated successfully")
            }
        }
    }

    private fun getKeyGenParameterSpecBuilder(alias: String): KeyGenParameterSpec.Builder {
        return KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(ENCRYPTION_KEY_SIZE_IN_BITS)
    }

    private fun createKey(keyGenerator: KeyGenerator, alias: String, useStrongBox: Boolean) {
        val builder = getKeyGenParameterSpecBuilder(alias)

        if (useStrongBox) {
            builder.setIsStrongBoxBacked(true)
        }

        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    override fun getSecretKey(): SecretKey {
        val keyStore = getKeyStore()
        return if (keyStore.containsAlias(STRONG_BOX_ALIAS)) {
            keyStore.getKey(STRONG_BOX_ALIAS, null) as SecretKey
        } else {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        }
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore" // this value should not change
        const val KEY_ALIAS = "PeraAESKey"
        const val STRONG_BOX_ALIAS = "${KEY_ALIAS}_strongbox"
        const val STRONG_BOX_TEST_ALIAS = "StrongBoxTest"
        const val ENCRYPTION_KEY_SIZE_IN_BITS = 256
        val TAG: String = AndroidEncryptionManager::class.java.simpleName
    }
}
