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

import com.algorand.backup.domain.model.Argon2idHash
import com.algorand.backup.domain.model.BackupMnemonicMismatchException
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.backup.domain.security.BackupKeyDerivationManager
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class ValidateBackupMnemonicForAddressUseCase @Inject constructor(
    private val keyDerivationManager: BackupKeyDerivationManager
) : ValidateBackupMnemonicForAddress {

    override fun invoke(mnemonic: String, argon2idHash: Argon2idHash, expectedAddress: String): PeraResult<Unit> {
        val input = KeyDerivationInput(
            mnemonic = mnemonic,
            salt = argon2idHash.salt,
            argon2idConfig = argon2idHash.config
        )
        return when (val result = keyDerivationManager.deriveKeys(input, expectedAddress)) {
            is PeraResult.Success -> {
                result.data.use { keyMaterial ->
                    if (keyMaterial.backupId.address == expectedAddress) {
                        PeraResult.Success(Unit)
                    } else {
                        PeraResult.Error(BackupMnemonicMismatchException())
                    }
                }
            }
            is PeraResult.Error -> PeraResult.Error(result.exception)
        }
    }
}
