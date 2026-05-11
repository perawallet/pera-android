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

import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import javax.inject.Inject

internal class GetBackupWalletPrivateKeyUseCase @Inject constructor(
    private val peraBip39Sdk: PeraBip39Sdk
) : GetBackupWalletPrivateKey {

    override fun invoke(mnemonic: String): ByteArray? {
        val entropy = peraBip39Sdk.getEntropyFromMnemonic(mnemonic) ?: return null
        return peraBip39Sdk.getSeedFromEntropy(entropy)
    }
}
