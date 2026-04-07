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

import com.algorand.backup.domain.model.AesGcmEncryptionResult
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class DefaultPeraCipher @Inject constructor() : PeraCipher {

    override fun encrypt(key: SecretKey, plaintext: ByteArray, aad: ByteArray?): AesGcmEncryptionResult {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        aad?.let { cipher.updateAAD(it) }
        return AesGcmEncryptionResult(
            iv = cipher.iv,
            ciphertext = cipher.doFinal(plaintext)
        )
    }

    override fun decrypt(key: SecretKey, ciphertext: ByteArray, iv: ByteArray, aad: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        aad?.let { cipher.updateAAD(it) }
        return cipher.doFinal(ciphertext)
    }

    private companion object {
        const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH_BITS = 128
    }
}
