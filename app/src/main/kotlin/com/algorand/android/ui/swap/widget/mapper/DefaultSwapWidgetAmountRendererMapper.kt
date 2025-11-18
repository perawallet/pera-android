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

package com.algorand.android.ui.swap.widget.mapper

import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.DecimalConfig.MinDecimalType.FixedToMax
import com.algorand.android.ui.common.amount.FormattedAmount
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.utils.emptyString
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuoteV2.AssetAmount
import com.algorand.wallet.swap.domain.model.SwapQuoteV2.AssetDetail
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultSwapWidgetAmountRendererMapper @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val parityUseCase: ParityUseCase
) : SwapWidgetAmountRendererMapper {

    override fun getDefaultRenderers(useLocalCurrency: Boolean): ViewState.Content.AmountRenderers {
        val localCurrencyAmountRenderer = getDefaultAmountRenderer(useLocalCurrency = true)
        val assetAmountRenderer = getDefaultAmountRenderer(useLocalCurrency = false)
        val primaryAmountRenderer = if (useLocalCurrency) localCurrencyAmountRenderer else assetAmountRenderer
        val secondaryAmountRenderer = if (useLocalCurrency) assetAmountRenderer else localCurrencyAmountRenderer
        return ViewState.Content.AmountRenderers(
            assetInPrimaryAmountHint = primaryAmountRenderer,
            assetInSecondaryAmount = secondaryAmountRenderer,
            assetOutPrimaryAmount = primaryAmountRenderer,
            assetOutSecondaryAmount = secondaryAmountRenderer
        )
    }

    override fun getQuoteRenderers(quote: SwapQuoteV2, useLocalCurrency: Boolean): ViewState.Content.AmountRenderers {
        val assetInSecondaryAmount = getFormattedAmount(useLocalCurrency, quote.assetInAmount, quote.assetInDetail)
        val assetOutPrimaryAmount = getFormattedAmount(!useLocalCurrency, quote.assetOutAmount, quote.assetOutDetail)
        val assetOutSecondaryAmount = getFormattedAmount(useLocalCurrency, quote.assetOutAmount, quote.assetOutDetail)
        val primaryAmountRendererPrefix = getAmountRendererPrefix(useLocalCurrency)
        val secondaryAmountRendererPrefix = getAmountRendererPrefix(!useLocalCurrency)
        return ViewState.Content.AmountRenderers(
            assetInPrimaryAmountHint = getDefaultAmountRenderer(useLocalCurrency),
            assetInSecondaryAmount = AmountRenderer(assetInSecondaryAmount, Plain, secondaryAmountRendererPrefix),
            assetOutPrimaryAmount = AmountRenderer(assetOutPrimaryAmount, Plain, primaryAmountRendererPrefix),
            assetOutSecondaryAmount = AmountRenderer(assetOutSecondaryAmount, Plain, secondaryAmountRendererPrefix)
        )
    }

    private fun getFormattedAmount(useCurrency: Boolean, amount: AssetAmount, detail: AssetDetail): FormattedAmount {
        return if (useCurrency) {
            SimplePlainFormattedAmount(PeraAmount(amount.amount), DecimalConfig(detail.fractionDecimals))
        } else {
            SimplePlainFormattedAmount(
                PeraAmount(amount.amountInUsdValue.multiply(getUsdToSelectedCurrencyConversionRate())),
                DecimalConfig(2, FixedToMax)
            )
        }
    }

    private fun getAmountRendererPrefix(useLocalCurrency: Boolean): String {
        return if (useLocalCurrency) parityUseCase.getDisplayedCurrencySymbol() else emptyString()
    }

    private fun getUsdToSelectedCurrencyConversionRate(): BigDecimal {
        return if (isPrimaryCurrencyAlgo()) {
            getUsdToSecondaryCurrencyConversionRate()
        } else {
            getUsdToPrimaryCurrencyConversionRate()
        }
    }

    private fun getDefaultAmountRenderer(useLocalCurrency: Boolean): AmountRenderer {
        val prefix = getAmountRendererPrefix(useLocalCurrency)
        val zeroAmount = PeraAmount(BigDecimal.ZERO)
        val formattedAmount = SimplePlainFormattedAmount(zeroAmount, DecimalConfig(2, FixedToMax))
        return AmountRenderer(formattedAmount, Plain, prefix = prefix)
    }
}
