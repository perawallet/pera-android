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

package com.algorand.wallet.account.core.domain.usecase

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import javax.inject.Inject

internal class AddHdSeedUseCase @Inject constructor(
    private val hdSeedRepository: HdSeedRepository
) : AddHdSeed {

    override suspend fun invoke(entropy: ByteArray): Int {
        val mnemonic = Mnemonics.MnemonicCode(entropy)
        val seed = mnemonic.toSeed()
        val entropy = mnemonic.toEntropy()
        val entropyInitialCustomName = "insert"

        val seedId = hdSeedRepository.addHdSeed(
            hdSeed = HdSeed(
                0,
                entropyInitialCustomName
            ), // Seed will be auto-generated, update name next step
            seed = seed,
            entropy = entropy
        ).toInt()

        hdSeedRepository.setEntropyCustomName(seedId, "Wallet #${seedId}")

        return seedId
    }
}
