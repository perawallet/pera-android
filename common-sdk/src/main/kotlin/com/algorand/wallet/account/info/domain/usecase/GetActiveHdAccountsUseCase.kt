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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.mapper.HdAccountAddressMapper
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressLite
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

internal class GetActiveHdAccountsUseCase @Inject constructor(
    private val bip39WalletProvider: Bip39WalletProvider,
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch,
    private val hdAccountAddressMapper: HdAccountAddressMapper
) : GetActiveHdAccounts {

    override suspend fun invoke(entropy: ByteArray): List<ActiveHdAccount> {
        val activeHdAccounts = mutableListOf<ActiveHdAccount>()
        var accountIndex = 0
        val bip39Api = bip39WalletProvider.getBip39Wallet(entropy)
        while (true) {
            val activeAccounts = getActiveAccountsBatchDeferred(accountIndex, entropy, bip39Api)
            if (activeAccounts.isEmpty()) {
                break
            } else {
                activeHdAccounts.addAll(activeAccounts)
                accountIndex += SEARCH_BATCH_COUNT
            }
        }
        bip39Api.invalidate()
        return activeHdAccounts
    }

    private suspend fun getActiveAccountsBatchDeferred(
        accountIndex: Int,
        entropy: ByteArray,
        bip39Wallet: Bip39Wallet
    ): List<ActiveHdAccount> {
        return supervisorScope {
            (accountIndex until accountIndex + SEARCH_BATCH_COUNT).map { index ->
                async {
                    getActiveHdAccountIfExist(index, entropy, bip39Wallet)
                }
            }
        }.awaitAll().filterNotNull()
    }

    private suspend fun getActiveHdAccountIfExist(
        accountIndex: Int,
        entropy: ByteArray,
        bip39Wallet: Bip39Wallet
    ): ActiveHdAccount? {
        val firstBatchHdKeyDetails = getFirstHdKeyDetailsBatch(accountIndex, bip39Wallet)
        val addresses = firstBatchHdKeyDetails.map { it.address }
        val firstBatchAccountFastLookup = getAccountFastLookupBatch(addresses)
        val isAccountActive = firstBatchAccountFastLookup.any { it.value?.accountExists == true }
        return if (isAccountActive) {
            val hdAccountAddresses = hdAccountAddressMapper(firstBatchHdKeyDetails, firstBatchAccountFastLookup)
            ActiveHdAccount(accountIndex, entropy, hdAccountAddresses)
        } else {
            null
        }
    }

    private fun getFirstHdKeyDetailsBatch(accountIndex: Int, bip39Wallet: Bip39Wallet): List<HdKeyAddressLite> {
        val range = 0 until SEARCH_BATCH_COUNT
        return range.map { keyIndex ->
            val index = HdKeyAddressIndex(accountIndex, keyIndex = keyIndex)
            bip39Wallet.generateAddressLite(index)
        }
    }

    private companion object {
        const val SEARCH_BATCH_COUNT = 5
    }
}
