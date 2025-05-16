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
import com.algorand.wallet.account.info.domain.model.HdKeyDetail
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import javax.inject.Inject

internal class GetActiveHdAccountsUseCase @Inject constructor(
    private val peraBip39Sdk: PeraBip39Sdk,
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch,
    private val hdAccountAddressMapper: HdAccountAddressMapper
) : GetActiveHdAccounts {

    override suspend fun invoke(entropy: ByteArray): List<ActiveHdAccount> {
        val activeHdAccounts = mutableListOf<ActiveHdAccount>()
        var accountIndex = 0
        while (true) {
            val firstBatchHdKeyDetails = getFirstHdKeyDetailsBatch(accountIndex, entropy)
            val addresses = firstBatchHdKeyDetails.map { it.algoAddress }
            val firstBatchAccountFastLookup = getAccountFastLookupBatch(addresses)
            val isAccountActive = firstBatchAccountFastLookup.any { it.value?.accountExists == true }
            if (isAccountActive) {
                val hdAccountAddresses = hdAccountAddressMapper(firstBatchHdKeyDetails, firstBatchAccountFastLookup)
                activeHdAccounts.add(ActiveHdAccount(accountIndex, entropy, hdAccountAddresses))
                accountIndex++
            } else {
                break
            }
        }
        return activeHdAccounts
    }

    private fun getFirstHdKeyDetailsBatch(accountIndex: Int, entropy: ByteArray): List<HdKeyDetail> {
        val range = 0 until SEARCH_BATCH_COUNT
        return range.map { keyIndex ->
            val address = peraBip39Sdk.generateHdKeyAddress(entropy, accountIndex, 0, keyIndex)
            HdKeyDetail(address, accountIndex, 0, keyIndex)
        }
    }

    private companion object {
        const val SEARCH_BATCH_COUNT = 5
    }
}
