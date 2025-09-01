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

package com.algorand.android.ui.asset.detail.view.holdings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.asset.detail.model.AssetLineChartData
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel.ViewState.Content
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraPercentageText
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.formatDateToChartDateString

@Composable
fun AssetHoldingBalanceStats(
    viewState: Content,
    selectedChartItem: AssetLineChartData?
) {
    val primaryAmountRenderer by remember(selectedChartItem) {
        mutableStateOf(selectedChartItem?.primaryAmountRenderer ?: viewState.balanceAmountRenderer)
    }
    val secondaryAmountRenderer by remember(selectedChartItem) {
        mutableStateOf(selectedChartItem?.secondaryAmountRenderer ?: viewState.balanceSelectedCurrencyRenderer)
    }

    val selectedDateText by remember(selectedChartItem) {
        val datetime = selectedChartItem?.datetime
        val text = if (datetime == null) emptyString() else formatDateToChartDateString(datetime)
        mutableStateOf(text)
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        PrimaryBalanceText(primaryAmountRenderer)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 20.dp)
        ) {
            Row(modifier = Modifier.weight(1f)) {
                SecondaryBalanceText(secondaryAmountRenderer)
                if (viewState.chartData != null && selectedChartItem == null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    BalanceChangeText(viewState.chartData)
                    Spacer(modifier = Modifier.width(8.dp))
                    ChangePercentageText(viewState.chartData)
                }
            }
            SelectedDateText(selectedDateText)
        }
    }
}

@Composable
private fun PrimaryBalanceText(renderer: AmountRenderer) {
    Text(
        text = renderer.getDisplayValue(),
        style = PeraTheme.typography.title.large.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun SecondaryBalanceText(renderer: AmountRenderer) {
    Text(
        text = renderer.getDisplayValue(),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun SelectedDateText(formattedDate: String) {
    Text(
        text = formattedDate,
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun BalanceChangeText(data: Content.ChartData) {
    val textColor = when {
        data.balanceChange > 0f -> PeraTheme.colors.helper.positive
        data.balanceChange < 0f -> PeraTheme.colors.helper.negative
        else -> PeraTheme.colors.text.gray
    }
    Text(
        text = data.balanceChangeRenderer.getDisplayValue(),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = textColor
    )
}

@Composable
private fun ChangePercentageText(data: Content.ChartData) {
    data.changePercentage?.let { percentage ->
        PeraPercentageText(percentage = percentage)
    }
}
