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

package com.algorand.android.ui.asset.detail.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.ui.asset.detail.model.AssetPriceHistoryItem
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel.ViewState.Error
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel.ViewState.Idle
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailPriceHistoryViewModel.ViewState.Loading
import com.algorand.android.ui.compose.widget.chart.view.PeraChartPeriodChip
import com.algorand.android.ui.compose.widget.chart.view.PeraChartPeriodContainer
import com.algorand.android.ui.compose.widget.chart.view.PeraLineChart
import com.algorand.android.ui.compose.widget.chart.view.PeraLineChartErrorState
import com.algorand.android.ui.compose.widget.chart.view.PeraLineChartLoadingState

@Composable
fun AssetPriceHistoryLineChart(
    assetId: Long,
    viewModel: AssetDetailPriceHistoryViewModel,
    onItemSelected: (AssetPriceHistoryItem) -> Unit,
    onItemDeselected: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
            Idle -> Unit
            Loading -> PeraLineChartLoadingState()
            is Content -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = CenterHorizontally
                ) {
                    when (state.contentState) {
                        ContentState.Loading -> PeraLineChartLoadingState(modifier = Modifier.weight(1f))
                        is ContentState.Data -> Chart(state.contentState, viewModel, onItemSelected, onItemDeselected)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PeraChartPeriodContainer {
                        state.periods.forEach { period ->
                            PeraChartPeriodChip(
                                text = stringResource(period.labelResId),
                                isSelected = state.selectedPeriod == period,
                                onClick = { viewModel.displaySelectedPeriodValues(period) }
                            )
                        }
                    }
                }
            }
            Error -> PeraLineChartErrorState()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init(assetId)
    }
}

@Composable
private fun ColumnScope.Chart(
    state: ContentState.Data,
    viewModel: AssetDetailPriceHistoryViewModel,
    onItemSelected: (AssetPriceHistoryItem) -> Unit,
    onItemDeselected: () -> Unit
) {
    PeraLineChart(
        modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp),
        data = state.chartData.map { it.usdPrice.toFloat() },
        onDataPointSelected = { index ->
            if (index == null) {
                onItemDeselected()
            } else {
                viewModel.getSelectedChartData(index)?.let(onItemSelected)
            }
        }
    )
}
