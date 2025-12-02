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

package com.algorand.android.modules.accountdetail.assets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountdetail.assets.ui.model.AddressLineChartData
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.android.ui.compose.widget.chart.mapper.ChartTendencyValuesMapper
import com.algorand.android.ui.compose.widget.chart.mapper.WalletWealthPeriodMapper
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartData
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneMonth
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneWeek
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneYear
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Idle
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import com.algorand.wallet.wealth.address.domain.model.AddressWealth
import com.algorand.wallet.wealth.address.domain.usecase.GetAddressWealth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class AccountAssetsLineChartViewModel @Inject constructor(
    private val walletWealthPeriodMapper: WalletWealthPeriodMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAddressWealth: GetAddressWealth,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val parityUseCase: ParityUseCase,
    private val tendencyValuesMapper: ChartTendencyValuesMapper
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, StatefulPeraLineChartViewModel {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    private val selectedPeriodFlow = MutableStateFlow<PeraLineChartPeriodChip>(INITIAL_CHART_PERIOD)

    fun init(address: String) {
        stateDelegate.onState<Idle> {
            stateDelegate.updateState {
                ViewState.Content(contentState = ContentState.Loading, INITIAL_CHART_PERIOD, PERIODS)
            }
            selectedPeriodFlow.onEach { period ->
                val viewState = getAddressWealth(
                    address = address,
                    period = walletWealthPeriodMapper(period),
                    currency = parityUseCase.getPrimaryFiatCurrencyId()
                ).use(
                    onSuccess = { addressWealth ->
                        val chartData = getAddressChartData(addressWealth)
                        val tendencyValues = tendencyValuesMapper(chartData)
                        ViewState.Content(ContentState.Data(chartData, tendencyValues), period, PERIODS)
                    },
                    onFailed = { _, _ ->
                        ViewState.Error
                    }
                )
                stateDelegate.updateState { viewState }
            }.launchIn(viewModelScope)
        }
    }

    override fun getSelectedChartData(index: Int): PeraLineChartData? {
        return ((state.value as? ViewState.Content)?.contentState as? ContentState.Data)
            ?.chartData
            ?.getOrNull(index)
    }

    override fun displaySelectedPeriodValues(period: PeraLineChartPeriodChip) {
        stateDelegate.onState<ViewState.Content> { currentState ->
            selectedPeriodFlow.value = period
            stateDelegate.updateState {
                currentState.copy(
                    contentState = ContentState.Loading,
                    selectedPeriod = period
                )
            }
        }
    }

    private fun getAddressChartData(addressWealth: AddressWealth): List<PeraLineChartData> {
        return addressWealth.chartData.map { chartData ->
            val primaryAmount: PeraAmount
            val secondaryAmount: PeraAmount

            val algoPeraAmount = PeraAmount(chartData.algoValue)
            val valueInCurrencyPeraAmount = PeraAmount(chartData.valueInCurrency)

            if (isPrimaryCurrencyAlgo()) {
                primaryAmount = algoPeraAmount
                secondaryAmount = valueInCurrencyPeraAmount
            } else {
                primaryAmount = valueInCurrencyPeraAmount
                secondaryAmount = algoPeraAmount
            }

            AddressLineChartData(
                datetime = chartData.datetime,
                primaryValue = chartData.valueInCurrency,
                primaryAmountRenderer = getCompactPrimaryAmountRenderer(primaryAmount, Plain),
                secondaryAmountRenderer = getCompactSecondaryAmountRenderer(secondaryAmount, Plain),
                round = chartData.round
            )
        }
    }

    private companion object {
        val INITIAL_CHART_PERIOD = OneWeek
        val PERIODS = listOf(OneWeek, OneMonth, OneYear)
    }
}
