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
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.CompactFormattedAmount
import com.algorand.android.ui.common.amount.PeraAmount
import java.math.BigDecimal
import javax.inject.Inject

internal class GetCompactPrimaryFiatAmountRendererUseCase @Inject constructor(
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
) : GetCompactPrimaryFiatAmountRenderer {

    override fun invoke(amount: PeraAmount, amountRendererType: RenderType): AmountRenderer {
        val isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo()
        val currencyConversionRate = getCurrencyConversionRate(isPrimaryCurrencyAlgo)
        val currencySymbol = getCurrencySymbol(isPrimaryCurrencyAlgo)
        val amountInSelectedCurrency = amount.value.multiply(currencyConversionRate)
        val primaryAmount = PeraAmount(amountInSelectedCurrency)
        val formattedAmount = CompactFormattedAmount(primaryAmount, CompactFormattedAmount.FractionalType.Fiat)
        return AmountRenderer(formattedAmount, amountRendererType, prefix = currencySymbol)
    }

    private fun getCurrencyConversionRate(isPrimaryCurrencyAlgo: Boolean): BigDecimal {
        return if (isPrimaryCurrencyAlgo) {
            getUsdToSecondaryCurrencyConversionRate()
        } else {
            getUsdToPrimaryCurrencyConversionRate()
        }
    }

    private fun getCurrencySymbol(isPrimaryCurrencyAlgo: Boolean): String {
        return if (isPrimaryCurrencyAlgo) {
            getSecondaryCurrencySymbol()
        } else {
            getPrimaryCurrencySymbolOrName()
        }
    }
}
