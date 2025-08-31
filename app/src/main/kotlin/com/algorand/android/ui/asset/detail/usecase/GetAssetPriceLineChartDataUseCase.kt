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

package com.algorand.android.ui.asset.detail.usecase

import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.ui.asset.detail.model.AssetPriceHistoryChartData
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount
import com.algorand.wallet.asset.pricehistory.domain.usecase.GetAssetPriceHistory
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import java.math.BigDecimal
import javax.inject.Inject

internal class GetAssetPriceLineChartDataUseCase @Inject constructor(
    private val getAssetPriceHistory: GetAssetPriceHistory,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
) : GetAssetPriceLineChartData {

    override suspend fun invoke(
        assetId: Long,
        period: WalletWealthPeriod
    ): PeraResult<List<AssetPriceHistoryChartData>> {
        return getAssetPriceHistory(assetId, period).map { priceHistory ->
            val isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo()
            val currencyConversionRate = getCurrencyConversionRate(isPrimaryCurrencyAlgo)
            val currencySymbol = getCurrencySymbol(isPrimaryCurrencyAlgo)
            priceHistory.map { chartData ->
                val amountInSelectedCurrency = chartData.price.multiply(currencyConversionRate)
                val primaryAmount = PeraAmount(amountInSelectedCurrency)
                val formattedAmount = PlainFormattedAmount.SimplePlainFormattedAmount(primaryAmount, DecimalConfig(2))
                AssetPriceHistoryChartData(
                    datetime = chartData.datetime,
                    primaryValue = primaryAmount.value,
                    primaryAmountRenderer = AmountRenderer(formattedAmount, Plain, prefix = currencySymbol)
                )
            }
        }
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
