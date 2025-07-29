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

package com.algorand.wallet.wealth.asset.data.mapper

import com.algorand.wallet.utils.date.parser.DateTimeParser
import com.algorand.wallet.wealth.asset.data.model.AssetBalanceHistoryResponseResult
import com.algorand.wallet.wealth.asset.data.model.AssetBalanceHistoryResponseResults
import com.algorand.wallet.wealth.asset.domain.model.AssetBalanceHistory
import com.algorand.wallet.wealth.asset.domain.model.AssetBalanceHistoryChartData

internal class DefaultAssetBalanceHistoryMapper(
    private val dateTimeParser: DateTimeParser
) : AssetBalanceHistoryMapper {

    override fun map(response: AssetBalanceHistoryResponseResults): AssetBalanceHistory {
        val chartData = response.results?.mapNotNull { mapToChartData(it) }.orEmpty()
        return AssetBalanceHistory(chartData)
    }

    private fun mapToChartData(response: AssetBalanceHistoryResponseResult): AssetBalanceHistoryChartData? {
        return with(response) {
            AssetBalanceHistoryChartData(
                datetime = dateTimeParser.parseOffsetDateTime(datetime.orEmpty()) ?: return null,
                usdValue = usdValue?.toBigDecimalOrNull() ?: return null,
                amount = amount?.toBigDecimalOrNull() ?: return null
            )
        }
    }
}
