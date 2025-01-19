/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class AESPlatformManagerImpl @Inject constructor() : AESPlatformManager {

    companion object {
        // TODO change values before account-refactor launch
        private const val KEY_ALIAS = "MyAESKey"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val AES_MODE = "AES/GCM/NoPadding"
    }

    init {
        generateKeyIfNeeded()
    }

    private fun generateKeyIfNeeded() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(parameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    override fun encryptByteArray(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)
        return iv + encryptedData // Append IV to the encrypted data
    }

    override fun decryptByteArray(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, 12) // First 12 bytes are the IV
        val cipherData = encryptedData.copyOfRange(12, encryptedData.size)
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return cipher.doFinal(cipherData)
    }

    // Encrypt a string
    @Throws(Exception::class)
    override fun encryptString(data: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv // GCM IV
        val encryptedBytes = cipher.doFinal(data.toByteArray())

        // Combine IV and ciphertext
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        // Return as Base64 string
        return Base64.getEncoder().encodeToString(combined)
    }

    // Decrypt a string
    @Throws(Exception::class)
    override fun decryptString(encryptedData: String): String {
        val combined = Base64.getDecoder().decode(encryptedData)

        // Extract IV and ciphertext
        val iv = ByteArray(12) // GCM standard IV length
        System.arraycopy(combined, 0, iv, 0, iv.size)
        val ciphertext = ByteArray(combined.size - iv.size)
        System.arraycopy(combined, iv.size, ciphertext, 0, ciphertext.size)

        // Decrypt
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv) // 128-bit authentication tag
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val plaintextBytes = cipher.doFinal(ciphertext)

        return String(plaintextBytes)
    }
}
