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

package com.algorand.android.ui.swap.providers.mapper

import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.FiatPlainFormattedAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import javax.inject.Inject

class DefaultSwapQuoteProviderSelectionItemMapper @Inject constructor(
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName
) : SwapQuoteProviderSelectionItemMapper {

    override fun invoke(swapQuote: SwapQuoteV2): SwapQuoteProviderSelectionItem.Provider {
        return SwapQuoteProviderSelectionItem.Provider(
            quoteId = swapQuote.quoteId,
            provider = swapQuote.provider,
            swapAmountRenderer = getAssetOutAmountRenderer(swapQuote),
            selectedCurrencyValueRenderer = getSelectedCurrencyValueRenderer(swapQuote)
        )
    }

    private fun getSelectedCurrencyValueRenderer(swapQuote: SwapQuoteV2): AmountRenderer {
        val currencyConversionRate = getUsdToPrimaryCurrencyConversionRate()
        val assetOutInUsdValue = swapQuote.assetOutAmount.amountInUsdValue
        val assetOutAmountInSelectedCurrency = PeraAmount(assetOutInUsdValue.multiply(currencyConversionRate))
        val formattedAmountInSelectedCurrency = FiatPlainFormattedAmount(assetOutAmountInSelectedCurrency)
        val suffix = getPrimaryCurrencySymbolOrName()
        return AmountRenderer(formattedAmountInSelectedCurrency, RenderType.Plain, suffix = suffix)
    }

    private fun getAssetOutAmountRenderer(swapQuote: SwapQuoteV2): AmountRenderer {
        val amount = PeraAmount(swapQuote.assetOutAmount.amount)
        val decimalConfig = DecimalConfig(swapQuote.assetOutDetail.fractionDecimals)
        val formattedAmount = SimplePlainFormattedAmount(amount, decimalConfig)
        return AmountRenderer(formattedAmount, RenderType.Plain)
    }
}
