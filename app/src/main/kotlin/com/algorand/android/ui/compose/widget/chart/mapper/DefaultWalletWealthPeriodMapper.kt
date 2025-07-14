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

import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartChip
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import javax.inject.Inject

internal class DefaultWalletWealthPeriodMapper @Inject constructor() : WalletWealthPeriodMapper {

    override fun invoke(period: PeraLineChartChip): WalletWealthPeriod {
        return when (period) {
            PeraLineChartPeriodChip.OneDay -> WalletWealthPeriod.ONE_DAY
            PeraLineChartPeriodChip.OneMonth -> WalletWealthPeriod.ONE_MONTH
            PeraLineChartPeriodChip.OneWeek -> WalletWealthPeriod.ONE_WEEK
            PeraLineChartPeriodChip.OneYear -> WalletWealthPeriod.ONE_YEAR
        }
    }
}
