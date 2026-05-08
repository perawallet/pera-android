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

import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultBackupKeyDerivationManager @Inject constructor(
    private val argonKeyManager: ArgonKeyManager,
    private val hkdfKeyManager: HkdfKeyManager,
    private val ed25519KeyManager: Ed25519KeyManager,
    private val mnemonicPasswordDeriver: BackupMnemonicPasswordDeriver
) : BackupKeyDerivationManager {

    override fun deriveKeys(input: KeyDerivationInput, walletAddress: String): PeraResult<DerivedKeyMaterial> {
        val masterKey = mnemonicPasswordDeriver.derive(input.mnemonic).use { password ->
            argonKeyManager.deriveMasterKey(password, input.salt, input.argon2idConfig)
        }

        return masterKey.use { mk ->
            val encryptionKey = hkdfKeyManager.deriveChildKey(mk, HKDF_INFO_ENCRYPTION_KEY)
            try {
                val authSeed = hkdfKeyManager.deriveChildKey(mk, HKDF_INFO_AUTH_SEED)
                val keyPair = authSeed.use { seed ->
                    ed25519KeyManager.deriveKeyPair(seed)
                }

                val backupId = BackupId.fromAddress(walletAddress)
                PeraResult.Success(
                    DerivedKeyMaterial(
                        backupId = backupId,
                        encryptionKey = encryptionKey,
                        authPrivateKey = keyPair.first,
                        authPublicKey = keyPair.second
                    )
                )
            } catch (e: Exception) {
                encryptionKey.close()
                throw e
            }
        }
    }

    private companion object {
        val HKDF_INFO_ENCRYPTION_KEY = "backup-encryption-key".toByteArray(Charsets.UTF_8)
        val HKDF_INFO_AUTH_SEED = "backup-auth-seed".toByteArray(Charsets.UTF_8)
    }
}
