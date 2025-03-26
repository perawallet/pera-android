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

import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.custom.domain.repository.CustomHdSeedInfoRepository
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.account.local.domain.usecase.GetSeedIdIfExistingEntropy
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class AddHdSeedUseCase @Inject constructor(
    private val hdSeedRepository: HdSeedRepository,
    private val customHdSeedInfoRepository: CustomHdSeedInfoRepository,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val getSeedIdIfExistingEntropy: GetSeedIdIfExistingEntropy,
    private val aesPlatformManager: AESPlatformManager
) : AddHdSeed {

    override suspend fun invoke(entropy: ByteArray): PeraResult<Int> {
        val existingSeedId = getSeedIdIfExistingEntropy.invoke(entropy)
        existingSeedId?.let {
            // seedId already exists in hd_seeds table
            return PeraResult.Success(it)
        } ?: run {
            // seedId doesn't exists in hd_seeds table
            var seed = peraBip39Sdk.getSeedFromEntropy(entropy)
            seed?.let {
                val newSeedIdInDB = hdSeedRepository.addHdSeed(
                    seedId = 0, // Seed will be auto-generated, update name next step
                    seed = it.copyOf(),
                    entropy = entropy
                ).toInt()

                customHdSeedInfoRepository.setCustomInfo(
                    CustomHdSeedInfo(
                        seedId = newSeedIdInDB,
                        entropyCustomName = "Wallet #${newSeedIdInDB}",
                        orderIndex = newSeedIdInDB,
                        isBackedUp = false
                    )
                )
                seed.clearFromMemory()
                return PeraResult.Success(newSeedIdInDB)
            } ?: run {
                return PeraResult.Error(Exception("Failed to insert hd seed"))
            }
        }
    }
}
