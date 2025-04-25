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

import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class GetRegisteredHdKeysUseCase @Inject constructor(
    private val getAccountFastLookup: GetAccountFastLookup,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val peraBip39Sdk: PeraBip39Sdk
) : GetRegisteredHdKeys {

    override suspend fun invoke(entropy: ByteArray): List<RegisteredHdKey> {
        val hdKeyDetails = getHdKeyDetailsToLookup(entropy)
        return withContext(Dispatchers.IO) {
            hdKeyDetails.map { hdKeyDetail ->
                async { getDeferredRegisteredHdKey(hdKeyDetail) }
            }.awaitAll().filterNotNull()
        }
    }

    private suspend fun getDeferredRegisteredHdKey(hdKeyDetail: HdKeyDetails): RegisteredHdKey? {
        return getAccountFastLookup(hdKeyDetail.algoAddress).getDataOrNull()?.let { accountFastLookup ->
            if (accountFastLookup.accountExists) {
                val isAlreadyImported = isThereAnyAccountWithAddress(hdKeyDetail.algoAddress)
                mapToRegisteredHdKey(hdKeyDetail, accountFastLookup, isAlreadyImported)
            } else {
                null
            }
        }
    }

    private fun mapToRegisteredHdKey(
        hdKeyDetails: HdKeyDetails,
        fastLookupAccount: AccountFastLookup,
        isAlreadyImported: Boolean
    ): RegisteredHdKey {
        return RegisteredHdKey(
            address = hdKeyDetails.algoAddress,
            algoValue = fastLookupAccount.algoValue,
            usdValue = fastLookupAccount.algoValue,
            accountExists = fastLookupAccount.accountExists,
            account = hdKeyDetails.accountIndex,
            change = hdKeyDetails.changeIndex,
            keyIndex = hdKeyDetails.keyIndex,
            isImportedToDB = isAlreadyImported
        )
    }

    private fun getHdKeyDetailsToLookup(entropy: ByteArray): List<HdKeyDetails> {
        val hdKeyDetailsList = mutableListOf<HdKeyDetails>()
        /**
         * Logic below creates 125 items. This class uses custom OkhttpClient which creates 125 connections
         * Update OkhttpClient accordingly if you change the number of items
         **/
        for (accountIndex in 0 until 5) {
            for (changeIndex in 0 until 5) {
                for (keyIndex in 0 until 5) {
                    val address = peraBip39Sdk.generateHdKeyAddress(entropy, accountIndex, changeIndex, keyIndex)
                    val hdKeyDetails = HdKeyDetails(address, accountIndex, changeIndex, keyIndex)
                    hdKeyDetailsList.add(hdKeyDetails)
                }
            }
        }
        return hdKeyDetailsList
    }

    private data class HdKeyDetails(
        val algoAddress: String,
        val accountIndex: Int,
        val changeIndex: Int,
        val keyIndex: Int
    )
}
