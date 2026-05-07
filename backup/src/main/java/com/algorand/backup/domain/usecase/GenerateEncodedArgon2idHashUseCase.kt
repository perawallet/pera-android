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

package com.algorand.backup.domain.usecase

import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.Argon2idHash
import com.algorand.backup.domain.security.Argon2idEncoder
import com.algorand.backup.domain.security.ArgonKeyManager
import com.algorand.backup.domain.security.BackupMnemonicPasswordDeriver
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class GenerateEncodedArgon2idHashUseCase @Inject constructor(
    private val revealBackupAuthCredentials: RevealBackupAuthCredentials,
    private val argonKeyManager: ArgonKeyManager,
    private val argon2idEncoder: Argon2idEncoder,
    private val mnemonicPasswordDeriver: BackupMnemonicPasswordDeriver
) : GenerateEncodedArgon2idHash {

    override fun invoke(): PeraResult<String> {
        return revealBackupAuthCredentials { _, mnemonic, salt ->
            val config = Argon2idConfig.DEFAULT
            mnemonicPasswordDeriver.derive(String(mnemonic.reveal(), Charsets.UTF_8)).use { password ->
                val masterKey = argonKeyManager.deriveMasterKey(password, salt, config)
                masterKey.use { mk ->
                    val hash = Argon2idHash(
                        version = ARGON2ID_VERSION,
                        config = config,
                        salt = salt,
                        hash = mk.reveal().copyOf()
                    )
                    argon2idEncoder.encode(hash)
                }
            }
        }
    }

    private companion object {
        const val ARGON2ID_VERSION = 19
    }
}
