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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.BuyAlgoButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.ReceiveButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SendButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SwapButton
import com.algorand.android.ui.asset.detail.model.AssetLineChartData
import com.algorand.android.ui.asset.detail.view.AssetDetailHeader
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetHoldingViewModel.ViewState.Idle
import com.algorand.android.ui.asset.detail.viewmodel.AssetLineChartViewModel
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChart
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChartListener
import com.algorand.android.ui.compose.widget.quickaction.BuySellQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.QuickActionButtonContainer
import com.algorand.android.ui.compose.widget.quickaction.ReceiveQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SendQuickActionButton
import com.algorand.android.ui.compose.widget.quickaction.SwapQuickActionButton
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel
import com.algorand.android.ui.transaction.history.view.TransactionHistoryListItemHeader
import com.algorand.android.ui.transaction.history.view.TransactionHistoryListListener
import com.algorand.android.ui.transaction.history.view.pagingTransactionHistoryListItems
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel.ViewState

@Composable
fun AssetHoldingScreen(
    assetDetailHeaderViewModel: AssetDetailHeaderViewModel,
    chartViewModel: AssetLineChartViewModel,
    assetHoldingViewModel: AssetHoldingViewModel,
    assetDetailViewModel: AssetDetailV2ViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    csvViewModel: CsvViewModel,
    listener: AssetHoldingScreenListener
) {
    val viewState = assetHoldingViewModel.state.collectAsStateWithLifecycle().value
    when (viewState) {
        Idle -> Unit
        is Content -> {
            var selectedChartItem by remember { mutableStateOf<AssetLineChartData?>(null) }
            val txnHistoryState = transactionHistoryViewModel.state.collectAsStateWithLifecycle()
            val historyItems = (txnHistoryState.value as? ViewState.Content)?.pagingData?.collectAsLazyPagingItems()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    AssetDetailHeader(assetDetailHeaderViewModel)
                }
                item {
                    AssetHoldingBalanceStats(viewState, selectedChartItem)
                }
                item {
                    BalanceHistoryChart(
                        chartViewModel,
                        onItemSelected = { selectedChartItem = it },
                        onItemDeselected = { selectedChartItem = null },
                        onChartDataUpdated = { assetHoldingViewModel.setChartData(it) }
                    )
                }
                item {
                    QuickActionButtons(viewState, assetDetailViewModel)
                }
                item {
                    Spacer(Modifier.height(40.dp))
                    TransactionHistoryListItemHeader(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        csvViewModel = csvViewModel,
                        isFilterSelected = transactionHistoryViewModel.isFilterSelected(),
                        onFilterClick = listener::onFilterClick,
                        onCsvClick = listener::onCsvClick
                    )
                }
                pagingTransactionHistoryListItems(historyItems, listener)
            }
        }
    }
}

@Composable
private fun BalanceHistoryChart(
    chartViewModel: AssetLineChartViewModel,
    onItemSelected: (AssetLineChartData) -> Unit,
    onItemDeselected: () -> Unit,
    onChartDataUpdated: (List<AssetLineChartData>) -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))
    val chartListener = remember {
        object : StatefulPeraLineChartListener {
            override fun onItemSelected(item: PeraLineChartData) {
                onItemSelected(item as AssetLineChartData)
            }

            override fun onItemDeselected() {
                onItemDeselected()
            }

            override fun onChartDataUpdated(items: List<PeraLineChartData>) {
                onChartDataUpdated(items as List<AssetLineChartData>)
            }
        }
    }
    StatefulPeraLineChart(modifier = Modifier.height(172.dp), chartViewModel, chartListener)
}

@Composable
private fun QuickActionButtons(viewState: Content, viewModel: AssetDetailV2ViewModel) {
    if (viewState.quickActionItems.isNotEmpty()) {
        Spacer(modifier = Modifier.height(32.dp))
        QuickActionButtonContainer {
            viewState.quickActionItems.forEach {
                when (it) {
                    is SwapButton -> SwapQuickActionButton(it.isSelected, viewModel::navigateToSwap)
                    BuyAlgoButton -> BuySellQuickActionButton(viewModel::navigateToOfframp)
                    ReceiveButton -> ReceiveQuickActionButton(viewModel::navigateToReceive)
                    SendButton -> SendQuickActionButton(viewModel::navigateToSend)
                }
            }
        }
    }
}

interface AssetHoldingScreenListener : TransactionHistoryListListener {
    fun onFilterClick()
    fun onCsvClick()
}
