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

import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.wallet.foundation.PeraResult
import java.security.MessageDigest
import javax.inject.Inject

internal class DefaultBackupKeyDerivationManager @Inject constructor(
    private val argonKeyManager: ArgonKeyManager,
    private val hkdfKeyManager: HkdfKeyManager,
    private val ed25519KeyManager: Ed25519KeyManager,
    private val backupIdManager: BackupIdManager
) : BackupKeyDerivationManager {

    override fun deriveKeys(input: KeyDerivationInput): PeraResult<DerivedKeyMaterial> {
        val masterKey = derivePasswordFromMnemonic(input.mnemonic).use { password ->
            argonKeyManager.deriveMasterKey(password, input.salt, input.argon2idConfig)
        }

        return masterKey.use { mk ->
            val encryptionKey = hkdfKeyManager.deriveChildKey(mk, HKDF_INFO_ENCRYPTION_KEY)
            try {
                val authSeed = hkdfKeyManager.deriveChildKey(mk, HKDF_INFO_AUTH_SEED)
                val keyPair = authSeed.use { seed ->
                    ed25519KeyManager.deriveKeyPair(seed)
                }

                when (val backupIdResult = backupIdManager.createBackupId(keyPair.second)) {
                    is PeraResult.Success -> {
                        PeraResult.Success(
                            DerivedKeyMaterial(
                                backupId = backupIdResult.data,
                                encryptionKey = encryptionKey,
                                authPrivateKey = keyPair.first,
                                authPublicKey = keyPair.second
                            )
                        )
                    }
                    is PeraResult.Error -> {
                        encryptionKey.close()
                        keyPair.first.close()
                        keyPair.second.close()
                        PeraResult.Error(backupIdResult.exception)
                    }
                }
            } catch (e: Exception) {
                encryptionKey.close()
                throw e
            }
        }
    }

    private fun derivePasswordFromMnemonic(mnemonic: String): SensitiveBytes {
        val normalized = mnemonic.trim().lowercase().split("\\s+".toRegex()).joinToString(" ")
        return SensitiveBytes(MessageDigest.getInstance(SHA_256).digest(normalized.toByteArray(Charsets.UTF_8)))
    }

    companion object {
        private const val SHA_256 = "SHA-256"

        private val HKDF_INFO_ENCRYPTION_KEY = "backup-encryption-key".toByteArray(Charsets.UTF_8)
        private val HKDF_INFO_AUTH_SEED = "backup-auth-seed".toByteArray(Charsets.UTF_8)
    }
}
