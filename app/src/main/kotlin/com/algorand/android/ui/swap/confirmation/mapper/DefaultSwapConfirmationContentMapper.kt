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

package com.algorand.android.ui.swap.confirmation.mapper

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.swap.common.SwapAppxValueParityHelper
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content
import com.algorand.android.utils.formatAsPercentage
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuoteV2.AssetDetail
import com.algorand.wallet.utils.divideOrZero
import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.FLOOR
import javax.inject.Inject

internal class DefaultSwapConfirmationContentMapper @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val priceImpactWarningStatusMapper: SwapPriceImpactWarningStatusMapper,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val swapAppxValueParityHelper: SwapAppxValueParityHelper
) : SwapConfirmationContentMapper {

    override suspend fun map(quote: SwapQuoteV2): Content {
        return Content(
            quote = quote,
            accountDisplayName = getAccountDisplayName(quote.accountAddress),
            accountIconDrawable = getAccountIconDrawablePreview(quote.accountAddress),
            priceImpact = priceImpactWarningStatusMapper(quote.priceImpact),
            assetInDetail = getAssetDetail(quote.assetInDetail, quote.assetInAmount),
            assetOutDetail = getAssetDetail(quote.assetOutDetail, quote.assetOutAmount),
            exchangeFee = getFeeRenderer(quote.fee.exchangeFeeAmount),
            peraFee = getFeeRenderer(quote.fee.peraFeeAmount),
            minReceivedAssetAmount = getAmountRenderer(quote.assetOutAmount.amountWithSlippage, quote.assetOutDetail),
            assetInToOutPriceRatio = getAssetInToOutPriceRatio(quote),
            assetOutToInPriceRatio = getAssetOutToInPriceRatio(quote),
            slippage = (quote.slippage * SLIPPAGE_MULTIPLIER).formatAsPercentage()
        )
    }

    private fun getAssetDetail(assetDetail: AssetDetail, assetAmount: SwapQuoteV2.AssetAmount): Content.AssetDetail {
        return with(assetDetail) {
            Content.AssetDetail(
                amount = getAmountRenderer(assetAmount.amount, this),
                approximateValue = getApproximateAmount(this, assetAmount),
                shortName = shortName,
                assetIconDrawable = assetIconDrawableMapper.map(this),
                verificationTier = verificationTierMapper.decideVerificationTierConfiguration(verificationTier)
            )
        }
    }

    private fun getAmountRenderer(amount: BigDecimal, assetDetail: AssetDetail): AmountRenderer {
        val decimalConfig = DecimalConfig(assetDetail.fractionDecimals)
        val formattedAmount = SimplePlainFormattedAmount(PeraAmount(amount), decimalConfig)
        return AmountRenderer(
            formattedAmount = formattedAmount,
            type = Plain,
            prefix = Currency.ALGO.symbol.takeIf { assetDetail.assetId == ALGO_ID },
            suffix = assetDetail.shortName.takeIf { assetDetail.assetId != ALGO_ID }
        )
    }

    private fun getApproximateAmount(assetDetail: AssetDetail, assetAmount: SwapQuoteV2.AssetAmount): AmountRenderer {
        val usdValuePerAsset = assetAmount.amountInUsdValue.divideOrZero(
            divisor = assetAmount.amount,
            roundingMode = RoundingMode.HALF_EVEN
        )
        val formattedAmount = swapAppxValueParityHelper.getDisplayedParityCurrencyValue(
            assetAmount = assetAmount.amount.movePointRight(assetDetail.fractionDecimals).toBigInteger(),
            assetUsdValue = usdValuePerAsset,
            assetDecimal = assetDetail.fractionDecimals,
            assetId = assetDetail.assetId
        ).getFormattedValue()
        return AmountRenderer(SimpleFormattedAmount(formattedAmount), Plain)
    }

    private fun getAssetInToOutPriceRatio(quote: SwapQuoteV2): Content.PriceRatio {
        return with(quote) {
            val ratio = assetInAmount.amount.divideOrZero(
                divisor = assetOutAmount.amount,
                scale = assetInDetail.fractionDecimals,
                roundingMode = FLOOR
            )
            val formattedRatio = SimplePlainFormattedAmount(
                PeraAmount(ratio),
                DecimalConfig(assetInDetail.fractionDecimals)
            )
            Content.PriceRatio(
                ratio = AmountRenderer(formattedAmount = formattedRatio, type = Plain),
                firstAssetUnitName = assetInDetail.shortName.orEmpty(),
                secondAssetUnitName = assetOutDetail.shortName.orEmpty()
            )
        }
    }

    private fun getAssetOutToInPriceRatio(quote: SwapQuoteV2): Content.PriceRatio {
        return with(quote) {
            val ratio = assetOutAmount.amount.divideOrZero(
                divisor = assetInAmount.amount,
                scale = assetOutDetail.fractionDecimals,
                roundingMode = FLOOR
            )
            val formattedRatio = SimplePlainFormattedAmount(
                PeraAmount(ratio),
                DecimalConfig(assetOutDetail.fractionDecimals)
            )
            Content.PriceRatio(
                ratio = AmountRenderer(formattedAmount = formattedRatio, type = Plain),
                firstAssetUnitName = assetOutDetail.shortName.orEmpty(),
                secondAssetUnitName = assetInDetail.shortName.orEmpty()
            )
        }
    }

    private fun getFeeRenderer(fee: BigDecimal): AmountRenderer {
        val feeAmount = PeraAmount(fee)
        val formattedAmount = PlainFormattedAmount.AlgoPlainFormattedAmount(feeAmount)
        return AmountRenderer(formattedAmount, Plain, prefix = Currency.ALGO.symbol)
    }

    private companion object {
        const val SLIPPAGE_MULTIPLIER = 100f
    }
}
