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

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.utils.MINUS_SIGN
import com.algorand.android.utils.PLUS_SIGN
import com.algorand.android.utils.emptyString
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.absoluteValue

internal class DefaultAmountDeltaAmountRendererMapper @Inject constructor() : AmountDeltaAmountRendererMapper {

    override fun invoke(delta: Float, prefix: String?): AmountRenderer {
        val sign = PLUS_SIGN.takeIf { delta > 0f } ?: MINUS_SIGN.takeIf { delta < 0f } ?: emptyString()
        val formattedChange = DecimalFormat().apply { maximumFractionDigits = 2 }.format(delta.absoluteValue)
        return AmountRenderer(
            formattedAmount = SimpleFormattedAmount(formattedChange),
            type = Plain,
            prefix = "$sign$prefix"
        )
    }
}
