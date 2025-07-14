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

package com.algorand.wallet.wealth.wallet.data.mapper

import com.algorand.wallet.utils.date.parser.DateTimeParser
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResult
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResults
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealth
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthChartData
import javax.inject.Inject

internal class DefaultWalletWealthMapper @Inject constructor(
    private val dateTimeParser: DateTimeParser
) : WalletWealthMapper {

    override fun map(response: WalletChartResponseResults): WalletWealth {
        val chartData = response.results?.mapNotNull { mapToChartData(it) }.orEmpty().sortedBy { it.round }
        return WalletWealth(chartData)
    }

    private fun mapToChartData(response: WalletChartResponseResult): WalletWealthChartData? {
        return with(response) {
            WalletWealthChartData(
                datetime = dateTimeParser.parseOffsetDateTime(datetime.orEmpty()) ?: return null,
                usdValue = usdValue?.toBigDecimalOrNull() ?: return null,
                algoValue = algoValue?.toBigDecimalOrNull() ?: return null,
                round = round?.toInt() ?: return null,
            )
        }
    }
}
