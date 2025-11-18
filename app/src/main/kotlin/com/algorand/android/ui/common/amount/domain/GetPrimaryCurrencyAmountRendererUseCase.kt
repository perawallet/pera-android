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

package com.algorand.android.ui.common.amount.domain

import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount
import java.math.BigDecimal
import javax.inject.Inject

internal class GetPrimaryCurrencyAmountRendererUseCase @Inject constructor(
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
) : GetPrimaryCurrencyAmountRenderer {

    override fun invoke(usdValue: BigDecimal, amount: BigDecimal, renderType: RenderType): AmountRenderer {
        val currencyConversionRate = getUsdToPrimaryCurrencyConversionRate()
        val currencySymbol = getPrimaryCurrencySymbolOrName()
        val amountInSelectedCurrency = usdValue.multiply(amount).multiply(currencyConversionRate)
        val primaryAmount = PeraAmount(amountInSelectedCurrency)
        val formattedAmount = PlainFormattedAmount.SimplePlainFormattedAmount(primaryAmount, DecimalConfig(2))
        return AmountRenderer(formattedAmount, Plain, prefix = currencySymbol)
    }
}
