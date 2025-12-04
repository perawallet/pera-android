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

package com.algorand.android.ui.accounts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.CurrencyCachingError
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Data
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.EmptyLocalAccounts
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.ui.accounts.model.AccountsLineChartData
import com.algorand.android.ui.accounts.usecase.GetAccountsLineChartData
import com.algorand.android.ui.accounts.usecase.GetFilteredPortfolioAccountLites
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
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import com.algorand.wallet.privacy.domain.model.PrivacyMode.Enabled
import com.algorand.wallet.privacy.domain.usecase.GetPrivacyModeFlow
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class AccountsLineChartViewModel @Inject constructor(
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val walletWealthPeriodMapper: WalletWealthPeriodMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountsLineChartData: GetAccountsLineChartData,
    private val getFilteredPortfolioAccountLites: GetFilteredPortfolioAccountLites,
    private val tendencyValuesMapper: ChartTendencyValuesMapper,
    private val getPrivacyModeFlow: GetPrivacyModeFlow
) : ViewModel(), StatefulPeraLineChartViewModel {

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    init {
        stateDelegate.setDefaultState(Idle)
    }

    private val selectedPeriodFlow = MutableStateFlow<PeraLineChartPeriodChip>(INITIAL_CHART_PERIOD)

    fun init() {
        stateDelegate.onState<Idle> {
            getAccountLiteCacheFlow()
                .mapLatest(::mapAccountLiteCacheMediatorState)
                .distinctUntilChanged()
                .combine(selectedPeriodFlow, ::mapChartStateMediatorState)
                .distinctUntilChanged()
                .combine(getPrivacyModeFlow(), ::updateViewState)
                .launchIn(viewModelScope)
        }
    }

    override fun getSelectedChartData(index: Int): AccountsLineChartData? {
        return ((state.value as? ViewState.Content)?.contentState as? ContentState.Data)
            ?.chartData
            ?.getOrNull(index) as? AccountsLineChartData
    }

    override fun displaySelectedPeriodValues(period: PeraLineChartPeriodChip) {
        selectedPeriodFlow.value = period
    }

    private fun mapAccountLiteCacheMediatorState(cacheStatus: AccountLiteCacheStatus): AccountCacheMediatorState {
        return when (cacheStatus) {
            EmptyLocalAccounts, AccountLiteCacheStatus.Idle -> AccountCacheMediatorState.Loading
            AccountLiteCacheStatus.Loading -> AccountCacheMediatorState.Loading
            is CurrencyCachingError -> AccountCacheMediatorState.Error
            is Data -> {
                val accountLites = getFilteredPortfolioAccountLites(cacheStatus.accountLites).keys.toList()
                AccountCacheMediatorState.Data(accountLites)
            }
        }
    }

    private suspend fun mapChartStateMediatorState(
        cacheMediator: AccountCacheMediatorState,
        selectedPeriod: PeraLineChartPeriodChip
    ): ChartMediatorState {
        return when (cacheMediator) {
            AccountCacheMediatorState.Loading -> ChartMediatorState.Loading
            AccountCacheMediatorState.Error -> ChartMediatorState.Error
            is AccountCacheMediatorState.Data -> {
                stateDelegate.updateState { ViewState.Content(ContentState.Loading, selectedPeriod, PERIODS) }
                getAccountsLineChartData(cacheMediator.authAddresses, walletWealthPeriodMapper(selectedPeriod)).use(
                    onSuccess = { ChartMediatorState.Data(it, selectedPeriod, PERIODS) },
                    onFailed = { _, _ -> ChartMediatorState.Error }
                )
            }
        }
    }

    private fun updateViewState(chartMediator: ChartMediatorState, privacyMode: PrivacyMode) {
        when (chartMediator) {
            ChartMediatorState.Loading -> stateDelegate.updateState { ViewState.Loading }
            ChartMediatorState.Error -> stateDelegate.updateState { ViewState.Error }
            is ChartMediatorState.Data -> {
                val tendencyValues = if (privacyMode is Enabled) null else tendencyValuesMapper(chartMediator.chartData)
                val contentState = ContentState.Data(chartMediator.chartData, tendencyValues)
                stateDelegate.updateState {
                    ViewState.Content(contentState, chartMediator.selectedPeriod, chartMediator.periods)
                }
            }
        }
    }

    private sealed interface AccountCacheMediatorState {
        data object Loading : AccountCacheMediatorState
        data object Error : AccountCacheMediatorState
        data class Data(val authAddresses: List<String>) : AccountCacheMediatorState
    }

    private sealed interface ChartMediatorState {
        data object Loading : ChartMediatorState
        data object Error : ChartMediatorState
        data class Data(
            val chartData: List<PeraLineChartData>,
            val selectedPeriod: PeraLineChartPeriodChip,
            val periods: List<PeraLineChartPeriodChip>
        ) : ChartMediatorState
    }

    private companion object {
        val INITIAL_CHART_PERIOD = OneWeek
        val PERIODS = listOf(OneWeek, OneMonth, OneYear)
    }
}
