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

import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.asset.detail.model.AssetLineChartData
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.wallet.asset.domain.util.AssetConstants
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.asset.domain.usecase.GetAssetBalanceHistory
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import javax.inject.Inject

class GetAssetLineChartDataUseCase @Inject constructor(
    private val getAssetBalanceHistory: GetAssetBalanceHistory,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
) : GetAssetLineChartData {

    override suspend fun invoke(
        address: String,
        assetId: Long,
        period: WalletWealthPeriod
    ): PeraResult<List<AssetLineChartData>> {
        return getAssetBalanceHistory(address, assetId, period).map { assetBalanceHistory ->
            assetBalanceHistory.chartData.map { chartData ->

                val primaryAmount = PeraAmount(chartData.amount)
                val secondaryAmount = PeraAmount(chartData.usdValue.multiply(getUsdToPrimaryCurrencyConversionRate()))

                val primaryRenderer = getCompactPrimaryAmountRenderer(primaryAmount, Plain)
                val secondaryRenderer = getCompactSecondaryAmountRenderer(secondaryAmount, Plain)

                if (assetId == 0L && getPrimaryCurrencySymbol() == AssetConstants.ALGO_SHORT_NAME) {
                    AssetLineChartData(
                        datetime = chartData.datetime,
                        primaryValue = primaryAmount.value,
                        primaryAmountRenderer = primaryRenderer,
                        secondaryAmountRenderer = secondaryRenderer,
                    )
                } else {
                    AssetLineChartData(
                        datetime = chartData.datetime,
                        primaryValue = primaryAmount.value,
                        primaryAmountRenderer = primaryRenderer,
                        secondaryAmountRenderer = primaryRenderer,
                    )
                }
            }
        }
    }
}
