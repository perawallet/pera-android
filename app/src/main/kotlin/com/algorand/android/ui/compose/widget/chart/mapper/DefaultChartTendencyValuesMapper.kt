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

package com.algorand.android.ui.compose.widget.chart.mapper

import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.ui.compose.widget.chart.extensions.getChangePercentage
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content.ContentState.Data.ChartTendencyValues
import com.algorand.android.utils.MINUS_SIGN
import com.algorand.android.utils.PLUS_SIGN
import com.algorand.android.utils.emptyString
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.absoluteValue

internal class DefaultChartTendencyValuesMapper @Inject constructor(
    private val parityUseCase: ParityUseCase
) : ChartTendencyValuesMapper {

    override fun invoke(items: List<PeraLineChartData>): ChartTendencyValues? {
        return if (items.size > 2) {
            val balanceDelta = items.last().value - items.first().value
            ChartTendencyValues(
                delta = balanceDelta,
                deltaRenderer = getDeltaRenderer(balanceDelta, parityUseCase.getDisplayedCurrencySymbol()),
                percentage = items.getChangePercentage()
            )
        } else {
            null
        }
    }

    private fun getDeltaRenderer(delta: Float, deltaCurrency: String): AmountRenderer {
        val sign = PLUS_SIGN.takeIf { delta > 0f } ?: MINUS_SIGN.takeIf { delta < 0f } ?: emptyString()
        val formattedChange = DecimalFormat().apply { maximumFractionDigits = 2 }.format(delta.absoluteValue)
        return AmountRenderer(
            formattedAmount = SimpleFormattedAmount(formattedChange),
            type = Plain,
            prefix = "$sign$deltaCurrency"
        )
    }
}
