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

package com.algorand.wallet.encryption.domain.manager

import com.algorand.wallet.encryption.domain.usecase.GetEncryptionSecretKey
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class AESPlatformManagerImpl @Inject constructor(
    private val getEncryptionSecretKey: GetEncryptionSecretKey
) : AESPlatformManager {

    companion object {
        private const val AES_MODE = "AES/GCM/NoPadding"
    }

    override fun encryptByteArray(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getEncryptionSecretKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)
        return iv + encryptedData // Append IV to the encrypted data
    }

    override fun decryptByteArray(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, 12) // First 12 bytes are the IV
        val cipherData = encryptedData.copyOfRange(12, encryptedData.size)
        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getEncryptionSecretKey(), spec)
        return cipher.doFinal(cipherData)
    }

    @Throws(Exception::class)
    override fun encryptString(data: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getEncryptionSecretKey())
        val iv = cipher.iv // GCM IV
        val encryptedBytes = cipher.doFinal(data.toByteArray())

        // Combine IV and ciphertext
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        // Return as Base64 string
        return Base64.getEncoder().encodeToString(combined)
    }

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
        cipher.init(Cipher.DECRYPT_MODE, getEncryptionSecretKey(), spec)
        val plaintextBytes = cipher.doFinal(ciphertext)

        return String(plaintextBytes)
    }
}
