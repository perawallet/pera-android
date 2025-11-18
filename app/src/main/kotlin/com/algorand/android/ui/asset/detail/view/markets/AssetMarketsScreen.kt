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

package com.algorand.android.ui.asset.detail.view.markets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetPriceHistoryChartData
import com.algorand.android.ui.asset.detail.view.AssetDetailHeader
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetMarketsViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetPriceLineChartViewModel
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraPercentageText
import com.algorand.android.ui.compose.widget.chart.extensions.getChangePercentage
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChart
import com.algorand.android.ui.compose.widget.chart.view.StatefulPeraLineChartListener
import com.algorand.android.utils.formatDateToChartDateString

@Composable
fun AssetMarketsScreen(
    assetDetailHeaderViewModel: AssetDetailHeaderViewModel,
    viewModel: AssetMarketsViewModel,
    chartViewModel: AssetPriceLineChartViewModel,
    listener: AssetMarketsScreenListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
            AssetMarketsViewModel.ViewState.Idle -> Unit
            is AssetMarketsViewModel.ViewState.Content -> {
                var priceRenderer by remember { mutableStateOf(viewState.assetPriceRenderer) }
                var selectedDateText by remember { mutableStateOf("") }
                var changePercentage by remember { mutableStateOf<Float?>(null) }
                var isChangePercentageVisible by remember { mutableStateOf(false) }
                Spacer(modifier = Modifier.height(32.dp))
                AssetDetailHeader(assetDetailHeaderViewModel)
                Spacer(modifier = Modifier.height(8.dp))
                PriceText(priceRenderer)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (isChangePercentageVisible) {
                        changePercentage?.let { percentage ->
                            PeraPercentageText(modifier = Modifier.padding(start = 24.dp), percentage)
                        }
                    }
                    SelectedDateText(selectedDateText)
                }
                Spacer(modifier = Modifier.height(20.dp))
                AssetPriceHistoryChart(
                    chartViewModel,
                    onItemSelected = {
                        priceRenderer = it.primaryAmountRenderer
                        selectedDateText = formatDateToChartDateString(it.datetime)
                        isChangePercentageVisible = false
                    },
                    onItemDeselected = {
                        priceRenderer = viewState.assetPriceRenderer
                        selectedDateText = ""
                        isChangePercentageVisible = true
                    },
                    onChartDataUpdated = { changePercentage = it.getChangePercentage() }
                )
                if (viewState.isAvailableOnDiscover) {
                    Spacer(modifier = Modifier.height(32.dp))
                    ViewOnDiscover()
                }
                AssetMarketsDetailsContainer(viewState.details, listener)
            }
        }
    }
}

@Composable
private fun BoxScope.SelectedDateText(selectedDateText: String) {
    Text(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .align(Alignment.CenterEnd),
        text = selectedDateText,
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AssetPriceHistoryChart(
    chartViewModel: AssetPriceLineChartViewModel,
    onItemSelected: (AssetPriceHistoryChartData) -> Unit,
    onItemDeselected: () -> Unit,
    onChartDataUpdated: (List<PeraLineChartData>) -> Unit
) {
    val chartListener = remember {
        object : StatefulPeraLineChartListener {
            override fun onItemSelected(item: PeraLineChartData) {
                (item as? AssetPriceHistoryChartData)?.let {
                    onItemSelected(item)
                }
            }

            override fun onItemDeselected() {
                onItemDeselected()
            }

            override fun onChartDataUpdated(items: List<PeraLineChartData>) {
                onChartDataUpdated(items)
            }
        }
    }
    StatefulPeraLineChart(modifier = Modifier.height(172.dp), chartViewModel, chartListener)
}

@Composable
private fun PriceText(amountRenderer: AmountRenderer) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = amountRenderer.getDisplayValue(),
        style = PeraTheme.typography.title.large.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun ViewOnDiscover() {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "View more details on Discover",
            style = PeraTheme.typography.footnote.sans,
            color = PeraTheme.colors.text.gray
        )
        Text(
            text = stringResource(R.string.markets),
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Icon(
            modifier = Modifier
                .size(20.dp)
                .padding(4.dp),
            painter = painterResource(R.drawable.ic_right_arrow),
            tint = PeraTheme.colors.text.grayLighter,
            contentDescription = null
        )
    }
}

interface AssetMarketsScreenListener : AssetMarketsDetailsListener
