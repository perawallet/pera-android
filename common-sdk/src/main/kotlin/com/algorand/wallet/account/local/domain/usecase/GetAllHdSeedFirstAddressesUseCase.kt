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

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.wallet.account.local.domain.model.HdSeedFirstAddress
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.foundation.security.SensitiveDataApi
import javax.inject.Inject

internal class GetAllHdSeedFirstAddressesUseCase @Inject constructor(
    private val hdSeedRepository: HdSeedRepository,
    private val bip39WalletProvider: Bip39WalletProvider
) : GetAllHdSeedFirstAddresses {

    @OptIn(SensitiveDataApi::class)
    override suspend fun invoke(): List<HdSeedFirstAddress> {
        val firstAddressIndex = HdKeyAddressIndex()
        return hdSeedRepository.getAllHdEntropies().mapNotNull { hdEntropy ->
            try {
                val wallet = bip39WalletProvider.getBip39Wallet(hdEntropy.entropy)
                val firstAddress = wallet.generateAddressLite(firstAddressIndex)
                wallet.invalidate()
                HdSeedFirstAddress(hdEntropy.seedId, firstAddress.address)
            } catch (e: Exception) {
                null
            } finally {
                hdEntropy.invalidate()
            }
        }
    }
}
