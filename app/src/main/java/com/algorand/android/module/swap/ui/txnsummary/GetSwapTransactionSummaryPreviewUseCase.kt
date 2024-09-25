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

package com.algorand.android.module.swap.ui.txnsummary

import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.assetutils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.foundation.common.isGreaterThan
import com.algorand.android.module.swap.ui.txnsummary.model.BaseSwapTransactionSummaryItem
import com.algorand.android.module.swap.ui.txnsummary.model.BaseSwapTransactionSummaryItem.SwapAccountItemTransaction
import com.algorand.android.module.swap.ui.txnsummary.model.BaseSwapTransactionSummaryItem.SwapAmountsItemTransaction
import com.algorand.android.module.swap.ui.txnsummary.model.BaseSwapTransactionSummaryItem.SwapFeesItemTransaction
import com.algorand.android.module.swap.ui.txnsummary.model.BaseSwapTransactionSummaryItem.SwapPriceImpactItemTransaction
import com.algorand.android.module.swap.ui.txnsummary.model.SwapTransactionSummaryPreview
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAssetAmount
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal
import javax.inject.Inject

internal class GetSwapTransactionSummaryPreviewUseCase @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : GetSwapTransactionSummaryPreview {

    override suspend fun invoke(
        swapQuote: SwapQuote,
        algorandTransactionFees: Long,
        optInTransactionFees: Long
    ): SwapTransactionSummaryPreview {
        val swapSummaryListItems = mutableListOf<BaseSwapTransactionSummaryItem>(
            createSwapAmountsItem(swapQuote),
            createSwapAccountItem(swapQuote),
            createSwapFeesItem(swapQuote, algorandTransactionFees, optInTransactionFees),
            createSwapPriceImpactItem(swapQuote)
        )
        return SwapTransactionSummaryPreview(swapSummaryListItems)
    }

    private fun createSwapAmountsItem(swapQuote: SwapQuote): SwapAmountsItemTransaction {
        with(swapQuote) {
            val fromAssetFractionalDigit = fromAssetDetail.fractionDecimals
            val toAssetFractionalDigit = toAssetDetail.fractionDecimals
            val formattedReceivedAmount = toAssetAmount
                .movePointLeft(toAssetFractionalDigit)
                .formatAmount(toAssetFractionalDigit, isDecimalFixed = false)
                .formatAsAssetAmount(getAssetSymbol(isToAssetAlgo, toAssetDetail.shortName), "+")
            val formattedPaidAmount = fromAssetAmount
                .movePointLeft(fromAssetFractionalDigit)
                .formatAmount(fromAssetFractionalDigit, isDecimalFixed = false)
                .run {
                    val transactionSign = "-"
                    if (isFromAssetAlgo) {
                        formatAsAlgoAmount(transactionSign)
                    } else {
                        formatAsAssetAmount(getAssetSymbol(isFromAssetAlgo, fromAssetDetail.shortName), transactionSign)
                    }
                }
            return SwapAmountsItemTransaction(formattedReceivedAmount, formattedPaidAmount)
        }
    }

    private suspend fun createSwapAccountItem(swapQuote: SwapQuote): SwapAccountItemTransaction {
        return SwapAccountItemTransaction(
            accountDisplayName = getAccountDisplayName(swapQuote.accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(swapQuote.accountAddress)
        )
    }

    private fun createSwapFeesItem(
        swapQuote: SwapQuote,
        algorandTransactionFees: Long,
        optInTransactionFees: Long
    ): SwapFeesItemTransaction {
        val peraFee = swapQuote.peraFeeAmount
        val exchangeFee = swapQuote.exchangeFeeAmount
        val formattedAlgorandTransactionFee = algorandTransactionFees
            .toBigDecimal()
            .movePointLeft(ALGO_DECIMALS)
            .formatAsCurrency(Currency.ALGO.symbol)
        val formattedOptInTransactionFee = optInTransactionFees
            .toBigDecimal()
            .movePointLeft(ALGO_DECIMALS)
            .formatAsCurrency(Currency.ALGO.symbol)
        val formattedExchangeFee = exchangeFee.formatAsCurrency(Currency.ALGO.symbol)
        val formattedPeraFee = peraFee.formatAsCurrency(Currency.ALGO.symbol)
        return SwapFeesItemTransaction(
            formattedAlgorandFees = formattedAlgorandTransactionFee,
            formattedOptInFees = formattedOptInTransactionFee,
            isOptInFeesVisible = optInTransactionFees > 0L,
            formattedExchangeFees = formattedExchangeFee,
            isExchangeFeesVisible = exchangeFee isGreaterThan BigDecimal.ZERO,
            formattedPeraFees = formattedPeraFee,
            isPeraFeeVisible = peraFee isGreaterThan BigDecimal.ZERO
        )
    }

    private fun createSwapPriceImpactItem(swapQuote: SwapQuote): SwapPriceImpactItemTransaction {
        return SwapPriceImpactItemTransaction(swapQuote.priceImpact.toString())
    }

    private fun getAssetSymbol(isAlgo: Boolean, assetName: String): String {
        return if (isAlgo) Currency.ALGO.symbol else getAssetName(assetName).assetName
    }
}
