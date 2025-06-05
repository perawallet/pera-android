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
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount.HdAccountAddress
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressLite
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import javax.inject.Inject

internal class GetActiveHdAccountAddressesUseCase @Inject constructor(
    private val bip39WalletProvider: Bip39WalletProvider,
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch,
    private val hdAccountAddressMapper: HdAccountAddressMapper
) : GetActiveHdAccountAddresses {

    override suspend fun invoke(activeHdAccount: ActiveHdAccount): List<HdAccountAddress> {
        val accountIndex = activeHdAccount.accountIndex
        val hdKeyDetailsList = mutableListOf<HdAccountAddress>().apply {
            addAll(activeHdAccount.firstBatchHdAccountAddress)
        }
        var rangeStart = SEARCH_BATCH_COUNT
        val bip39Api = bip39WalletProvider.getBip39Wallet(activeHdAccount.entropy)
        while (true) {
            val hdKeyDetailsBatch = createHdKeyDetailBatch(bip39Api, accountIndex, getSearchBatchRange(rangeStart))
            val addresses = hdKeyDetailsBatch.map { it.address }
            val accountFastLookupBatch = getAccountFastLookupBatch(addresses)
            if (shouldContinueSearching(accountFastLookupBatch)) {
                val hdAccountAddresses = hdAccountAddressMapper(hdKeyDetailsBatch, accountFastLookupBatch)
                hdKeyDetailsList.addAll(hdAccountAddresses)
                rangeStart += SEARCH_BATCH_COUNT
            } else {
                break
            }
        }
        bip39Api.invalidate()
        return hdKeyDetailsList
    }

    private fun shouldContinueSearching(accountFastLookupBatch: Map<String, AccountFastLookup?>): Boolean {
        return accountFastLookupBatch.values.any { it?.accountExists == true }
    }

    private fun createHdKeyDetailBatch(bip39Wallet: Bip39Wallet, accountIndex: Int, range: IntRange): List<HdKeyAddressLite> {
        return range.map { keyIndex ->
            val index = HdKeyAddressIndex(accountIndex, 0, keyIndex)
            bip39Wallet.generateAddressLite(index)
        }
    }

    private fun getSearchBatchRange(rangeStart: Int): IntRange {
        return rangeStart until rangeStart + SEARCH_BATCH_COUNT
    }

    private companion object {
        const val SEARCH_BATCH_COUNT = 5
    }
}
