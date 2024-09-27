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

package com.algorand.android.module.swap.ui.confirmswap.usecase

import com.algorand.android.module.asset.utils.AssetConstants
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction.SwapTransaction
import com.algorand.android.module.swap.component.domain.usecase.GetSwapAlgorandTransactionFee
import com.algorand.android.module.swap.component.domain.usecase.GetSwapOptInTransactionFee
import com.algorand.android.module.swap.ui.txnstatus.model.SignedSwapTransaction
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusNavArgs
import com.algorand.android.utils.formatAmount
import java.math.BigInteger
import javax.inject.Inject

internal class GetSwapTransactionStatusNavArgsUseCase @Inject constructor(
    private val getSwapAlgorandTransactionFee: GetSwapAlgorandTransactionFee,
    private val getSwapOptInTransactionFee: GetSwapOptInTransactionFee,
) : GetSwapTransactionStatusNavArgs {

    override fun invoke(
        swapQuote: SwapQuote,
        signedTransactions: List<SwapQuoteTransaction>
    ): SwapTransactionStatusNavArgs {
        return SwapTransactionStatusNavArgs(
            swapQuote = swapQuote,
            optInTransactionsFees = getSwapOptInTransactionFee(signedTransactions.toTypedArray()),
            algorandTransactionsFees = getSwapAlgorandTransactionFee(signedTransactions.toTypedArray()),
            transactionGroupId = signedTransactions.firstOrNull { it is SwapTransaction }?.transactionGroupId,
            formattedReceivedAssetAmount = getFormattedReceivedAssetAmount(swapQuote, signedTransactions),
            networkFeesAsAlgo = getNetworkFeeAsAlgo(signedTransactions),
            signedSwapTransactions = signedTransactions.map {
                SignedSwapTransaction(it.getSignedTransactionsByteArray(), it.isTransactionConfirmationNeed)
            }
        )
    }

    private fun getNetworkFeeAsAlgo(signedTransactions: List<SwapQuoteTransaction>): Double {
        val algorandTransactionFee = getSwapAlgorandTransactionFee(signedTransactions.toTypedArray())
        val optInTransactionFee = getSwapOptInTransactionFee(signedTransactions.toTypedArray())
        val networkFee = algorandTransactionFee + optInTransactionFee
        return networkFee.toBigDecimal()
            .movePointLeft(AssetConstants.ALGO_DECIMALS)
            .stripTrailingZeros()
            .toDouble()
    }

    private fun getFormattedReceivedAssetAmount(
        swapQuote: SwapQuote,
        signedTransactions: List<SwapQuoteTransaction>
    ): String {
        val swapTransaction = signedTransactions.firstOrNull { it is SwapTransaction } as? SwapTransaction
        var receivedAssetAmount: BigInteger? = null
        swapTransaction?.unsignedTransactions?.forEach {
            if (it.decodedTransaction?.receiverAddress?.decodedAddress == it.accountAddress) {
                receivedAssetAmount = it.decodedTransaction.amount?.toBigInteger() ?: BigInteger.ZERO
                return@forEach
            } else if (it.decodedTransaction?.assetReceiverAddress?.decodedAddress == it.accountAddress) {
                receivedAssetAmount = it.decodedTransaction.assetAmount ?: BigInteger.ZERO
                return@forEach
            }
        }

        val receivedAssetDecimal = swapQuote.toAssetDetail.fractionDecimals
        return receivedAssetAmount
            ?.toBigDecimal()
            ?.movePointLeft(receivedAssetDecimal)
            ?.formatAmount(receivedAssetDecimal, isDecimalFixed = false)
            ?: swapQuote.getFormattedMinimumReceivedAmount()
    }
}
