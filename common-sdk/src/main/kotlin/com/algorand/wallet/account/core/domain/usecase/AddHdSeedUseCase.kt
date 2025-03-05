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

import com.algorand.wallet.account.info.domain.model.EntropyInformation
import com.algorand.wallet.account.info.domain.repository.EntropyInformationRepository
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.algosdk.transaction.sdk.Bip39MnemonicGenerator
import javax.inject.Inject

internal class AddHdSeedUseCase @Inject constructor(
    private val hdSeedRepository: HdSeedRepository,
    private val entropyInformationRepository: EntropyInformationRepository,
    private val bip39MnemonicGenerator: Bip39MnemonicGenerator
) : AddHdSeed {

    override suspend fun invoke(entropy: ByteArray): Int {
        var seed = bip39MnemonicGenerator.getSeedFromEntropy(entropy)
        seed?.let {
            val newSeedIdInDB = hdSeedRepository.addHdSeed(
                seedId = 0, // Seed will be auto-generated, update name next step
                seed = it,
                entropy = entropy
            ).toInt()

            entropyInformationRepository.addEntropyInformation(
                EntropyInformation(
                    seedId = newSeedIdInDB,
                    entropyCustomName = "Wallet #${newSeedIdInDB}"
                ) )
            seed = ByteArray(0)
            return newSeedIdInDB
        } ?: run {
            return -1
        }
    }
}
