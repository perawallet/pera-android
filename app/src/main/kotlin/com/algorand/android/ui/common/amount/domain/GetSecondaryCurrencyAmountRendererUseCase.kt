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

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.wallet.asset.domain.model.Asset
import java.math.BigInteger
import javax.inject.Inject

internal class GetSecondaryCurrencyAmountRendererUseCase @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
) : GetSecondaryCurrencyAmountRenderer {

    override fun invoke(asset: Asset, amount: BigInteger, renderType: RenderType): AmountRenderer {
        val displaySecondaryCurrency = asset.isAlgo && isPrimaryCurrencyAlgo()
        val amountInSelectedCurrency = getAmountInSecondaryCurrency(displaySecondaryCurrency, asset, amount)
        val decimalConfig = getDecimalConfig(asset, displaySecondaryCurrency)
        return AmountRenderer(
            formattedAmount = SimplePlainFormattedAmount(amountInSelectedCurrency, decimalConfig),
            type = renderType,
            prefix = getPrefix(displaySecondaryCurrency)
        )
    }

    private fun getPrefix(displaySecondaryCurrency: Boolean): String {
        return if (displaySecondaryCurrency) Currency.USD.symbol else getPrimaryCurrencySymbolOrName()
    }

    private fun getDecimalConfig(asset: Asset, displaySecondaryCurrency: Boolean): DecimalConfig {
        val decimals = if (displaySecondaryCurrency) FIAT_DECIMAL else asset.getDecimalsOrZero()
        return DecimalConfig(maxDecimals = decimals)
    }

    private fun getAmountInSecondaryCurrency(
        displaySecondaryCurrency: Boolean,
        asset: Asset,
        amount: BigInteger
    ): PeraAmount {
        val decimals = asset.getDecimalsOrZero()
        val assetAmount = amount.toBigDecimal().movePointLeft(decimals)
        val conversionRate = if (displaySecondaryCurrency) {
            getUsdToSecondaryCurrencyConversionRate()
        } else {
            getUsdToPrimaryCurrencyConversionRate()
        }
        return PeraAmount(assetAmount.multiply(asset.usdValue).multiply(conversionRate))
    }

    private companion object {
        const val FIAT_DECIMAL = 2
    }
}
