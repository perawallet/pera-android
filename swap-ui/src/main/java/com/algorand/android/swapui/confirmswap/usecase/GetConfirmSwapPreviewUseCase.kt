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

package com.algorand.android.swapui.confirmswap.usecase

import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.formatting.formatAmount
import com.algorand.android.formatting.formatAsAlgoAmount
import com.algorand.android.formatting.formatAsAssetAmount
import com.algorand.android.formatting.formatAsCurrency
import com.algorand.android.formatting.formatAsPercentage
import com.algorand.android.parity.domain.usecase.GetUsdValuePerAsset
import com.algorand.android.swap.data.mapper.DisplayedCurrencyParityValueMapper
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swap.domain.model.SwapQuoteAssetDetail
import com.algorand.android.swapui.common.getFormattedMinimumReceivedAmount
import com.algorand.android.swapui.confirmswap.mapper.ConfirmSwapPriceImpactWarningStatusMapper
import com.algorand.android.swapui.confirmswap.mapper.SwapAssetDetailMapper
import com.algorand.android.swapui.confirmswap.mapper.SwapPriceRatioProviderMapper
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPreview
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus.NoWarning
import java.math.BigDecimal
import javax.inject.Inject

internal class GetConfirmSwapPreviewUseCase @Inject constructor(
    private val getAssetName: GetAssetName,
    private val confirmSwapPriceImpactWarningStatusMapper: ConfirmSwapPriceImpactWarningStatusMapper,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getUsdValuePerAsset: GetUsdValuePerAsset,
    private val swapAssetDetailMapper: SwapAssetDetailMapper,
    private val displayedCurrencyParityValueMapper: DisplayedCurrencyParityValueMapper,
    private val swapPriceRatioProviderMapper: SwapPriceRatioProviderMapper
) : GetConfirmSwapPreview {

    override suspend fun invoke(swapQuote: SwapQuote): ConfirmSwapPreview {
        return with(swapQuote) {
            ConfirmSwapPreview(
                fromAssetDetail = createFromAssetDetail(swapQuote),
                toAssetDetail = createToAssetDetail(swapQuote),
                priceRatioProvider = swapPriceRatioProviderMapper(swapQuote),
                slippageTolerance = slippage.formatAsPercentage(),
                formattedPriceImpact = priceImpact.formatAsPercentage(),
                minimumReceived = getFormattedMinimumReceivedAmount(swapQuote),
                formattedPeraFee = peraFeeAmount.formatAsCurrency(Currency.ALGO.symbol),
                formattedExchangeFee = getFormattedExchangeFee(swapQuote),
                swapQuote = swapQuote,
                isLoading = false,
                errorEvent = null,
                slippageToleranceUpdateSuccessEvent = null,
                accountIconDrawablePreview = getAccountIconDrawablePreview(swapQuote.accountAddress),
                accountDisplayName = getAccountDisplayName(swapQuote.accountAddress),
                priceImpactWarningStatus = confirmSwapPriceImpactWarningStatusMapper(swapQuote.priceImpact)
            )
        }
    }

    private suspend fun createFromAssetDetail(swapQuote: SwapQuote): ConfirmSwapPreview.SwapAssetDetail {
        return with(swapQuote) {
            createAssetDetail(fromAssetDetail, fromAssetAmount, fromAssetAmountInUsdValue, NoWarning)
        }
    }

    private suspend fun createToAssetDetail(swapQuote: SwapQuote): ConfirmSwapPreview.SwapAssetDetail {
        return with(swapQuote) {
            val priceImpactWarningStatus = confirmSwapPriceImpactWarningStatusMapper(swapQuote.priceImpact)
            createAssetDetail(toAssetDetail, toAssetAmount, toAssetAmountInUsdValue, priceImpactWarningStatus)
        }
    }

    private suspend fun createAssetDetail(
        assetDetail: SwapQuoteAssetDetail,
        amount: BigDecimal,
        approximateValueInUsd: BigDecimal,
        priceImpactWarningStatus: ConfirmSwapPriceImpactWarningStatus
    ): ConfirmSwapPreview.SwapAssetDetail {
        val formattedAmount = amount.movePointLeft(assetDetail.fractionDecimals)
            .formatAmount(assetDetail.fractionDecimals, isDecimalFixed = false)
        val amountTextColorResId = priceImpactWarningStatus.toAssetAmountTextColorResId
        return swapAssetDetailMapper(
            assetId = assetDetail.assetId,
            formattedAmount = formattedAmount,
            formattedApproximateValue = getFormattedApproximateValue(assetDetail, amount, approximateValueInUsd),
            shortName = getAssetName(assetDetail.shortName),
            verificationTier = assetDetail.verificationTier,
            amountTextColorResId = amountTextColorResId,
            approximateValueTextColorResId = amountTextColorResId
        )
    }

    private fun getFormattedExchangeFee(swapQuote: SwapQuote): String {
        return with(swapQuote) {
            exchangeFeeAmount.stripTrailingZeros().toPlainString().run {
                if (isFromAssetAlgo) {
                    formatAsAlgoAmount()
                } else {
                    formatAsAssetAmount(fromAssetDetail.shortName)
                }
            }
        }
    }

    private fun getFormattedApproximateValue(
        assetDetail: SwapQuoteAssetDetail,
        amount: BigDecimal,
        approximateValueInUsd: BigDecimal
    ): String {
        val usdValuePerAsset = getUsdValuePerAsset(
            amount.toPlainString(),
            assetDetail.fractionDecimals,
            approximateValueInUsd.toPlainString()
        )
        return displayedCurrencyParityValueMapper(
            assetAmount = amount.toBigInteger(),
            assetUsdValue = usdValuePerAsset,
            assetDecimal = assetDetail.fractionDecimals,
            assetId = assetDetail.assetId
        ).getFormattedValue()
    }
}
