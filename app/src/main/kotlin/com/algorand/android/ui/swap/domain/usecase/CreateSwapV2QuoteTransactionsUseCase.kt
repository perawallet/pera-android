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
import com.algorand.android.modules.swap.confirmswap.domain.model.SwapTransactionPurpose
import com.algorand.android.modules.swap.confirmswap.domain.model.UnsignedSwapSingleTransactionData
import com.algorand.android.ui.swap.domain.model.SwapQuoteTransactions
import com.algorand.android.ui.swap.domain.model.SwapQuoteTransactionsDto
import com.algorand.android.ui.swap.domain.repository.SwapQuoteTransactionsRepository
import com.algorand.android.usecase.NetworkSlugUseCase
import com.algorand.android.utils.decodeBase64
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.validation.SwapTransactionValidator
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import javax.inject.Inject

internal class CreateSwapV2QuoteTransactionsUseCase @Inject constructor(
    private val swapQuoteTransactionsRepository: SwapQuoteTransactionsRepository,
    private val unsignedSwapQuoteTransactionMapper: UnsignedSwapSingleTransactionDataMapper,
    private val signedSwapQuoteTransactionMapper: SignedSwapSingleTransactionDataMapper,
    private val swapTransactionItemFactory: SwapTransactionItemFactory,
    private val networkSlugUseCase: NetworkSlugUseCase,
    private val parseTransactionMsgPackUseCase: ParseTransactionMsgPackUseCase,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    private val swapTransactionValidator: SwapTransactionValidator,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val errorLogger: PeraErrorLogger
) : CreateSwapV2QuoteTransactions {

    override suspend fun invoke(quote: SwapQuoteV2, accountAddress: String): PeraResult<SwapQuoteTransactions> {
        return swapQuoteTransactionsRepository.createQuoteTransactions(quote.quoteId).use(
            onSuccess = { txns ->
                if (isFeatureToggleEnabled(FeatureToggle.SWAP_TXN_VALIDATION.key)) {
                    if (areTransactionsValid(quote, txns)) {
                        createSwapQuoteTransactionsResult(accountAddress, txns)
                    } else {
                        errorLogger.logError("Swap transaction validation failed for quoteId: ${quote.quoteId}")
                        PeraResult.Error(Exception())
                    }
                } else {
                    createSwapQuoteTransactionsResult(accountAddress, txns)
                }
            },
            onFailed = { exception, _ ->
                PeraResult.Error(exception)
            }
        )
    }

    private suspend fun createSwapQuoteTransactionsResult(
        accountAddress: String,
        transactionsDto: SwapQuoteTransactionsDto
    ): PeraResult<SwapQuoteTransactions> {
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
        return PeraResult.Success(SwapQuoteTransactions(transactions, transactionsDto.swapId))
    }

    private suspend fun areTransactionsValid(quote: SwapQuoteV2, txns: SwapQuoteTransactionsDto): Boolean {
        val swapTxns = txns.transactions.first { it.purpose == SwapTransactionPurpose.SWAP }
        val signedTxns = swapTxns.signedTransactions?.mapNotNull { it?.decodeBase64() }.orEmpty()
        val unsignedTxns = swapTxns.signedTransactions?.mapIndexedNotNull { index, txn ->
            if (txn == null) swapTxns.transactions?.get(index)?.decodeBase64() else null
        }.orEmpty()
        val data = SwapTransactionValidationData(quote, signedTxns, unsignedTxns, getLocalAccountsAddresses())
        return swapTransactionValidator.areTransactionsValid(data)
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
