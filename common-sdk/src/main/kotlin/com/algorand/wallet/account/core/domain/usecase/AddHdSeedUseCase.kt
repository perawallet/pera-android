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

import android.util.Log
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class AddHdSeedUseCase @Inject constructor(
    private val hdSeedRepository: HdSeedRepository
) : AddHdSeed {

    override fun invoke(mnemonic: Mnemonics.MnemonicCode): Flow<Int> {
        val encryptedSeed = mnemonic.toSeed()
        val encryptedEntropy = mnemonic.toEntropy()
        val entropyInitialCustomName = "insert"

        return flow {
            hdSeedRepository.addHdSeedAsFlow(
                hdSeed = HdSeed(0, entropyInitialCustomName), // Seed will be auto-generated, update later
                seed = encryptedSeed,
                entropy = encryptedEntropy
            ).collect {
                // After collecting the seedId, retrieve the entity
                val hdSeedEntities = hdSeedRepository.getAllHdSeed(entropyInitialCustomName)

                hdSeedEntities.forEach { hdSeedEntity ->
                    // Set custom name after the insert
                    hdSeedEntity.seedCustomName = "Wallet #${hdSeedEntity.seedId}"

                    hdSeedEntity.let {
                        hdSeedRepository.updateHdSeedCustomNameAsFlow(it)
                    }.collect {
                        Log.i(TAG, "${hdSeedEntity.seedId} updated with ${hdSeedEntity.seedCustomName}")
                    }
                }
                emit(hdSeedEntities.first().seedId)
            }
        }
    }

    companion object {
        private const val TAG: String = "AddHdSeedUseCase"
    }
}
