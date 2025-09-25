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

package com.algorand.android.ui.swap.domain.usecase

import com.algorand.android.modules.algosdk.domain.usecase.ParseTransactionMsgPackUseCase
import com.algorand.android.modules.swap.confirmswap.domain.factory.SwapTransactionItemFactory
import com.algorand.android.modules.swap.confirmswap.domain.mapper.SignedSwapSingleTransactionDataMapper
import com.algorand.android.modules.swap.confirmswap.domain.mapper.UnsignedSwapSingleTransactionDataMapper
import com.algorand.android.modules.swap.confirmswap.domain.model.SignedSwapSingleTransactionData
import com.algorand.android.modules.swap.confirmswap.domain.model.SwapQuoteTransactionDTO
import com.algorand.android.modules.swap.confirmswap.domain.model.UnsignedSwapSingleTransactionData
import com.algorand.android.ui.swap.domain.model.SwapQuoteTransactions
import com.algorand.android.ui.swap.domain.model.SwapQuoteTransactionsDto
import com.algorand.android.ui.swap.domain.repository.SwapQuoteTransactionsRepository
import com.algorand.android.usecase.NetworkSlugUseCase
import com.algorand.android.utils.decodeBase64
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class CreateSwapV2QuoteTransactionsUseCase @Inject constructor(
    private val swapQuoteTransactionsRepository: SwapQuoteTransactionsRepository,
    private val unsignedSwapQuoteTransactionMapper: UnsignedSwapSingleTransactionDataMapper,
    private val signedSwapQuoteTransactionMapper: SignedSwapSingleTransactionDataMapper,
    private val swapTransactionItemFactory: SwapTransactionItemFactory,
    private val networkSlugUseCase: NetworkSlugUseCase,
    private val parseTransactionMsgPackUseCase: ParseTransactionMsgPackUseCase,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) : CreateSwapV2QuoteTransactions {

    override suspend fun invoke(quoteId: Long, accountAddress: String): PeraResult<SwapQuoteTransactions> {
        return swapQuoteTransactionsRepository.createQuoteTransactions(quoteId).use(
            onSuccess = { txns ->
                val swapQuoteTransactions = createSwapQuoteTransactions(accountAddress, txns)
                PeraResult.Success(swapQuoteTransactions)
            },
            onFailed = { exception, _ ->
                PeraResult.Error(exception)
            }
        )
    }

    private suspend fun createSwapQuoteTransactions(
        accountAddress: String,
        transactionsDto: SwapQuoteTransactionsDto
    ): SwapQuoteTransactions {
        val transactions = transactionsDto.transactions.mapIndexed { parentListIndex, swapQuoteTransactionDTO ->
            val signedSingleTransactions = createSingleSignedTransactions(swapQuoteTransactionDTO, parentListIndex)
            val unsignedSingleTransactions = createUnsignedSingleTransactions(
                swapQuoteTransactionDTO = swapQuoteTransactionDTO,
                parentListIndex = parentListIndex,
                accountAddress = accountAddress
            )
            swapTransactionItemFactory.createTransaction(
                purpose = swapQuoteTransactionDTO.purpose,
                transactionGroupId = swapQuoteTransactionDTO.transactionGroupId,
                unsignedTransactions = unsignedSingleTransactions,
                signedTransactions = signedSingleTransactions,
                transactionNetworkSlug = networkSlugUseCase.getActiveNodeSlug().orEmpty()
            )
        }
        return SwapQuoteTransactions(transactions, transactionsDto.swapId)
    }

    private fun createSingleSignedTransactions(
        swapQuoteTransactionDTO: SwapQuoteTransactionDTO,
        parentListIndex: Int
    ): MutableList<SignedSwapSingleTransactionData> {
        return swapQuoteTransactionDTO.signedTransactions
            ?.mapIndexed { index, signedTransaction ->
                signedSwapQuoteTransactionMapper.mapToSignedSwapSingleTransactionData(
                    parentListIndex = parentListIndex,
                    transactionListIndex = index,
                    signedTransactionMsgPack = signedTransaction?.decodeBase64()
                )
            }.orEmpty().toMutableList()
    }

    private suspend fun createUnsignedSingleTransactions(
        swapQuoteTransactionDTO: SwapQuoteTransactionDTO,
        parentListIndex: Int,
        accountAddress: String
    ): List<UnsignedSwapSingleTransactionData> {
        return swapQuoteTransactionDTO.transactions
            ?.mapIndexed { index, unsignedTransaction ->
                unsignedSwapQuoteTransactionMapper.mapToUnsignedSwapSingleTransactionData(
                    parentListIndex = parentListIndex,
                    transactionListIndex = index,
                    transactionMsgPack = unsignedTransaction,
                    accountAddress = accountAddress,
                    accountAuthAddress = getAccountRekeyAdminAddress(accountAddress),
                    rawTransaction = unsignedTransaction?.decodeBase64()
                        ?.run { parseTransactionMsgPackUseCase.parse(this) }
                )
            }.orEmpty()
    }
}
