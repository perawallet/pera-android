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

package com.algorand.wallet.algosdk.bip39.sdk

import cash.z.ecc.android.bip39.Mnemonics
import com.algorand.wallet.algosdk.bip39.model.Bip39Entropy
import javax.inject.Inject

internal class AlgorandBip39WalletProvider @Inject constructor() : Bip39WalletProvider {

    override fun getBip39Wallet(entropy: ByteArray): Bip39Wallet {
        return AlgorandBip39Wallet(Bip39Entropy(entropy))
    }

    override fun createBip39Wallet(): Bip39Wallet {
        val entropy = Mnemonics.MnemonicCode(Mnemonics.WordCount.COUNT_24).toEntropy()
        return AlgorandBip39Wallet(Bip39Entropy(entropy))
    }

    override fun create12WordBip39Wallet(): Bip39Wallet {
        val entropy = Mnemonics.MnemonicCode(Mnemonics.WordCount.COUNT_12).toEntropy()
        return AlgorandBip39Wallet(Bip39Entropy(entropy))
    }
}
