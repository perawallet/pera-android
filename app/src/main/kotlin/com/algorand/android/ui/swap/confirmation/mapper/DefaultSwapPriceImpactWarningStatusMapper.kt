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

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact
import java.math.RoundingMode.HALF_EVEN
import javax.inject.Inject

internal class DefaultSwapPriceImpactWarningStatusMapper @Inject constructor() : SwapPriceImpactWarningStatusMapper {

    override fun invoke(priceImpactPercentage: Float): SwapPriceImpact {
        val warningStatus = when {
            priceImpactPercentage < FIVE_PERCENT -> SwapPriceImpact.WarningStatus.NoWarning
            priceImpactPercentage < TEN_PERCENT -> SwapPriceImpact.WarningStatus.Level1(FIVE_PERCENT)
            priceImpactPercentage < FIFTEEN_PERCENT -> SwapPriceImpact.WarningStatus.Level2(TEN_PERCENT)
            else -> SwapPriceImpact.WarningStatus.Level3(FIFTEEN_PERCENT)
        }
        val percentage = priceImpactPercentage.toBigDecimal()
            .setScale(PRICE_IMPACT_PERCENTAGE_SCALE, HALF_EVEN)
            .toPlainString()
        val renderer = AmountRenderer(SimpleFormattedAmount("$percentage%"), Plain)
        return SwapPriceImpact(renderer, warningStatus)
    }

    private companion object {
        private const val FIVE_PERCENT = 5f
        private const val TEN_PERCENT = 10f
        private const val FIFTEEN_PERCENT = 15f
        private const val PRICE_IMPACT_PERCENTAGE_SCALE = 4
    }
}
