/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.component.domain.usecase

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.algosdk.component.transaction.ParseTransactionMessagePack
import com.algorand.android.foundation.PeraResult
import com.algorand.android.foundation.common.decodeBase64
import com.algorand.android.module.swap.component.domain.factory.SwapTransactionItemFactory
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SignedSwapSingleTransactionData
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransactionResult
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.UnsignedSwapSingleTransactionData
import com.algorand.android.module.swap.component.domain.repository.AssetSwapRepository
import javax.inject.Inject

internal class CreateSwapQuoteTransactionsUseCase @Inject constructor(
    private val assetSwapRepository: AssetSwapRepository,
    private val swapTransactionItemFactory: SwapTransactionItemFactory,
    private val getAccountInformation: GetAccountInformation,
    private val parseTransactionMessagePack: ParseTransactionMessagePack
) : CreateSwapQuoteTransactions {

    override suspend fun invoke(quoteId: Long, accountAddress: String): PeraResult<List<SwapQuoteTransaction>> {
        return assetSwapRepository.createQuoteTransactions(quoteId).map { result ->
            createSwapQuoteTransactionList(accountAddress, result)
        }
    }

    private suspend fun createSwapQuoteTransactionList(
        accountAddress: String,
        swapQuoteTransactionResults: List<SwapQuoteTransactionResult>
    ): List<SwapQuoteTransaction> {
        return swapQuoteTransactionResults.mapIndexed { parentListIndex, swapQuoteTransactionResult ->
            val signedSingleTransactions = createSingleSignedTransactions(swapQuoteTransactionResult, parentListIndex)
            val unsignedSingleTransactions = createUnsignedSingleTransactions(
                swapQuoteTransactionResult = swapQuoteTransactionResult,
                parentListIndex = parentListIndex,
                accountAddress = accountAddress
            )
            swapTransactionItemFactory.create(
                SwapTransactionItemFactory.Payload(
                    purpose = swapQuoteTransactionResult.purpose,
                    transactionGroupId = swapQuoteTransactionResult.transactionGroupId,
                    unsignedTransactions = unsignedSingleTransactions,
                    signedTransactions = signedSingleTransactions
                )
            )
        }
    }

    private fun createSingleSignedTransactions(
        swapQuoteTransactionResult: SwapQuoteTransactionResult,
        parentListIndex: Int
    ): MutableList<SignedSwapSingleTransactionData> {
        return swapQuoteTransactionResult.signedTransactions
            ?.mapIndexed { index, signedTransaction ->
                SignedSwapSingleTransactionData(
                    parentListIndex = parentListIndex,
                    transactionListIndex = index,
                    signedTransactionMsgPack = signedTransaction?.decodeBase64()
                )
            }.orEmpty().toMutableList()
    }

    private suspend fun createUnsignedSingleTransactions(
        swapQuoteTransactionResult: SwapQuoteTransactionResult,
        parentListIndex: Int,
        accountAddress: String
    ): List<UnsignedSwapSingleTransactionData> {
        return swapQuoteTransactionResult.transactions
            ?.mapIndexed { index, unsignedTransaction ->
                UnsignedSwapSingleTransactionData(
                    parentListIndex = parentListIndex,
                    transactionListIndex = index,
                    transactionMsgPack = unsignedTransaction,
                    accountAddress = accountAddress,
                    accountAuthAddress = getAccountAuthAddressIfExist(accountAddress),
                    decodedTransaction = unsignedTransaction?.decodeBase64()?.run {
                        parseTransactionMessagePack(this)
                    }
                )
            }.orEmpty()
    }

    private suspend fun getAccountAuthAddressIfExist(accountAddress: String): String? {
        return getAccountInformation(accountAddress)?.rekeyAdminAddress
    }
}
