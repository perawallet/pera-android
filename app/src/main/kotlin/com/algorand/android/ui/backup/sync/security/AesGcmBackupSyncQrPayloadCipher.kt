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

package com.algorand.android.ui.backup.sync.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

internal class AesGcmBackupSyncQrPayloadCipher @Inject constructor(
    private val serializer: BackupSyncQrPayloadSerializer
) : BackupSyncQrPayloadCipher {

    override fun encrypt(pin: String, payload: BackupSyncPayload): ByteArray {
        val random = SecureRandom()
        val kdfSalt = ByteArray(SALT_LENGTH_BYTES).also(random::nextBytes)
        val iv = ByteArray(IV_LENGTH_BYTES).also(random::nextBytes)
        val key = deriveKey(pin, kdfSalt)

        val plaintext = serializer.serialize(payload).toByteArray(Charsets.UTF_8)
        val cipher = Cipher.getInstance(AES_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        }
        val ciphertext = cipher.doFinal(plaintext)

        return ByteArray(kdfSalt.size + iv.size + ciphertext.size).apply {
            System.arraycopy(kdfSalt, 0, this, 0, kdfSalt.size)
            System.arraycopy(iv, 0, this, kdfSalt.size, iv.size)
            System.arraycopy(ciphertext, 0, this, kdfSalt.size + iv.size, ciphertext.size)
        }
    }

    override fun decrypt(pin: String, encrypted: ByteArray): Result<BackupSyncPayload> = runCatching {
        require(encrypted.size > SALT_LENGTH_BYTES + IV_LENGTH_BYTES) { "Sync payload truncated" }

        val kdfSalt = encrypted.copyOfRange(0, SALT_LENGTH_BYTES)
        val iv = encrypted.copyOfRange(SALT_LENGTH_BYTES, SALT_LENGTH_BYTES + IV_LENGTH_BYTES)
        val ciphertext = encrypted.copyOfRange(SALT_LENGTH_BYTES + IV_LENGTH_BYTES, encrypted.size)

        val key = deriveKey(pin, kdfSalt)
        val plaintext = Cipher.getInstance(AES_TRANSFORMATION).run {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
            doFinal(ciphertext)
        }
        serializer.deserialize(String(plaintext, Charsets.UTF_8))
    }

    private fun deriveKey(pin: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KDF_ALGORITHM)
        val spec = PBEKeySpec(pin.toCharArray(), salt, KDF_ITERATIONS, KEY_LENGTH_BITS)
        val rawKey = factory.generateSecret(spec).encoded
        return SecretKeySpec(rawKey, AES_KEY_ALGORITHM)
    }

    private companion object {
        const val KDF_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val KDF_ITERATIONS = 100_000
        const val KEY_LENGTH_BITS = 256
        const val SALT_LENGTH_BYTES = 16
        const val IV_LENGTH_BYTES = 12
        const val GCM_TAG_LENGTH_BITS = 128
        const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        const val AES_KEY_ALGORITHM = "AES"
    }
}
