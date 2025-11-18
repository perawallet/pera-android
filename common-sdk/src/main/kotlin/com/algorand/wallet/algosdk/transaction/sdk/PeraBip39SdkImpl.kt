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

package com.algorand.wallet.algosdk.transaction.sdk

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import javax.inject.Inject

internal class PeraBip39SdkImpl @Inject constructor() : PeraBip39Sdk {
    override fun getSeedFromEntropy(entropy: ByteArray): ByteArray? {
        return try {
            Mnemonics.MnemonicCode(entropy).toSeed()
        } catch (_: Exception) {
            null
        }
    }

    override fun getEntropyFromMnemonic(mnemonic: String): ByteArray? {
        return try {
            Mnemonics.MnemonicCode(mnemonic).toEntropy()
        } catch (_: Exception) {
            null
        }
    }

    override fun getMnemonicFromEntropy(entropy: ByteArray): String? {
        return try {
            val mnemonic = Mnemonics.MnemonicCode(entropy).words.joinToString(" ") { charArray ->
                String(charArray)
            }
            mnemonic
        } catch (_: Exception) {
            null
        }
    }
}
