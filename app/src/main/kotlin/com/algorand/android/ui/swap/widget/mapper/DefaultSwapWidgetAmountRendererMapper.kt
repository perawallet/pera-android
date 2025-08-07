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

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.DecimalConfig.MinDecimalType.FixedToMax
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.wallet.swap.domain.model.SwapQuote
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultSwapWidgetAmountRendererMapper @Inject constructor() : SwapWidgetAmountRendererMapper {

    override fun getDefaultRenderers(): ViewState.Content.AmountRenderers {
        val defaultAmountRenderer = getDefaultAmountRenderer()
        return ViewState.Content.AmountRenderers(
            assetInPrimaryAmountHint = defaultAmountRenderer,
            assetInSecondaryAmount = defaultAmountRenderer,
            assetOutPrimaryAmount = defaultAmountRenderer,
            assetOutSecondaryAmount = defaultAmountRenderer
        )
    }

    override fun getQuoteRenderers(quote: SwapQuote): ViewState.Content.AmountRenderers {
        // TODO don't use fixed to max or max decimals 2 when user selects local currency
        val assetInSecondaryAmount = SimplePlainFormattedAmount(
            PeraAmount(quote.assetInAmount.amountInUsdValue),
            DecimalConfig(2, FixedToMax)
        )
        val assetOutPrimaryAmount = SimplePlainFormattedAmount(
            PeraAmount(quote.assetOutAmount.amount),
            DecimalConfig(quote.assetOutDetail.fractionDecimals)
        )
        // TODO don't use fixed to max or max decimals 2 when user selects local currency
        val assetOutSecondaryAmount = SimplePlainFormattedAmount(
            PeraAmount(quote.assetOutAmount.amountInUsdValue),
            DecimalConfig(2, FixedToMax)
        )
        return ViewState.Content.AmountRenderers(
            assetInPrimaryAmountHint = getDefaultAmountRenderer(),
            assetInSecondaryAmount = AmountRenderer(assetInSecondaryAmount, AmountRenderer.RenderType.Plain),
            assetOutPrimaryAmount = AmountRenderer(assetOutPrimaryAmount, AmountRenderer.RenderType.Plain),
            assetOutSecondaryAmount = AmountRenderer(assetOutSecondaryAmount, AmountRenderer.RenderType.Plain)
        )
    }

    private fun getDefaultAmountRenderer(): AmountRenderer {
        val zeroAmount = PeraAmount(BigDecimal.ZERO)
        val formattedAmount = SimplePlainFormattedAmount(zeroAmount, DecimalConfig(2, FixedToMax))
        return AmountRenderer(formattedAmount, AmountRenderer.RenderType.Plain)
    }
}
