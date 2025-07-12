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

package com.algorand.android.ui.asset.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.ui.asset.detail.model.AssetPriceHistoryItem
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.compose.widget.chart.mapper.WalletWealthPeriodMapper
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneMonth
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneWeek
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneYear
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content.ContentState.Data
import com.algorand.wallet.asset.pricehistory.domain.model.AssetPriceHistory
import com.algorand.wallet.asset.pricehistory.domain.usecase.GetAssetPriceHistory
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class AssetDetailPriceHistoryViewModel @Inject constructor(
    private val getAssetPriceHistory: GetAssetPriceHistory,
    private val walletWealthPeriodMapper: WalletWealthPeriodMapper,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, StatefulPeraLineChartViewModel {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    private val selectedPeriodFlow = MutableStateFlow<PeraLineChartPeriodChip>(INITIAL_CHART_PERIOD)

    fun init(assetId: Long) {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState { Content(contentState = ContentState.Loading, INITIAL_CHART_PERIOD, PERIODS) }
            selectedPeriodFlow.onEach { period ->
                val viewState = getAssetPriceHistory(assetId, walletWealthPeriodMapper(period)).use(
                    onSuccess = { history -> Content(Data(getAssetPriceHistoryItems(history)), period, PERIODS) },
                    onFailed = { _, _ -> ViewState.Error }
                )
                stateDelegate.updateState { viewState }
            }.launchIn(viewModelScope)
        }
    }

    override fun displaySelectedPeriodValues(period: PeraLineChartPeriodChip) {
        stateDelegate.onState<Content> { currentState ->
            selectedPeriodFlow.value = period
            stateDelegate.updateState {
                currentState.copy(contentState = ContentState.Loading, selectedPeriod = period)
            }
        }
    }

    override fun getSelectedChartData(index: Int): PeraLineChartData? {
        return ((state.value as? Content)?.contentState as? Data)?.chartData?.getOrNull(index)
    }

    private fun getAssetPriceHistoryItems(assetPriceHistory: List<AssetPriceHistory>): List<PeraLineChartData> {
        val usdToSelectedCurrencyRate = getUsdToPrimaryCurrencyConversionRate()
        return assetPriceHistory.map { history ->
            val selectedCurrencyPrice = history.price.multiply(usdToSelectedCurrencyRate)
            val amountRendered = getCompactPrimaryAmountRenderer(PeraAmount(selectedCurrencyPrice), Plain)
            AssetPriceHistoryItem(
                datetime = history.datetime,
                usdPrice = selectedCurrencyPrice,
                formattedPriceInSelectedCurrency = amountRendered.getDisplayValue()
            )
        }
    }

    private companion object {
        val INITIAL_CHART_PERIOD = OneWeek
        val PERIODS = listOf(OneWeek, OneMonth, OneYear)
    }
}
