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
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.CurrencyCachingError
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Data
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.EmptyLocalAccounts
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.ui.accounts.model.AccountsLineChartData
import com.algorand.android.ui.accounts.usecase.GetAccountsLineChartData
import com.algorand.android.ui.compose.widget.chart.mapper.WalletWealthPeriodMapper
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneDay
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneMonth
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneWeek
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartPeriodChip.OneYear
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Content.ContentState
import com.algorand.android.ui.compose.widget.chart.viewmodel.StatefulPeraLineChartViewModel.ViewState.Idle
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

@HiltViewModel
class AccountsLineChartViewModel @Inject constructor(
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val walletWealthPeriodMapper: WalletWealthPeriodMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountsLineChartData: GetAccountsLineChartData
) : ViewModel(), StatefulPeraLineChartViewModel {

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    init {
        stateDelegate.setDefaultState(Idle)
    }

    private val selectedPeriodFlow = MutableStateFlow<PeraLineChartPeriodChip>(INITIAL_CHART_PERIOD)

    fun init() {
        stateDelegate.onState<Idle> {
            combine(getAccountLiteCacheFlow(), selectedPeriodFlow) { accountLiteCacheStatus, selectedPeriod ->
                when (accountLiteCacheStatus) {
                    EmptyLocalAccounts, AccountLiteCacheStatus.Idle -> Idle
                    AccountLiteCacheStatus.Loading -> stateDelegate.updateState { ViewState.Loading }
                    is CurrencyCachingError -> stateDelegate.updateState { ViewState.Error }
                    is Data -> updateAccountChartState(accountLiteCacheStatus.accountLites, selectedPeriod)
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun getSelectedChartData(index: Int): AccountsLineChartData? {
        return ((state.value as? ViewState.Content)?.contentState as? ContentState.Data)
            ?.chartData
            ?.getOrNull(index)
            as? AccountsLineChartData
    }

    override fun displaySelectedPeriodValues(period: PeraLineChartPeriodChip) {
        selectedPeriodFlow.value = period
    }

    private suspend fun updateAccountChartState(
        accountLites: Map<String, AccountLite>,
        selectedPeriod: PeraLineChartPeriodChip
    ) {
        stateDelegate.updateState { ViewState.Content(ContentState.Loading, selectedPeriod, PERIODS) }
        val authAddresses = getAuthAddressesOrNull(accountLites)
        if (authAddresses == null) {
            stateDelegate.updateState { ViewState.Error }
            return
        } else {
            val viewState = getAccountsLineChartData(authAddresses, walletWealthPeriodMapper(selectedPeriod)).use(
                onSuccess = {
                    ViewState.Content(ContentState.Data(it), selectedPeriod, PERIODS)
                },
                onFailed = { _, _ ->
                    ViewState.Error
                }
            )
            stateDelegate.updateState { viewState }
        }
    }

    private fun getAuthAddressesOrNull(accountLites: Map<String, AccountLite>): List<String>? {
        return accountLites.values.mapNotNull { accountLite ->
            if (accountLite.cachedInfo == null) return null
            accountLite.address.takeIf { accountLite.cachedInfo.type.canSignTransaction() }
        }
    }

    private companion object {
        val INITIAL_CHART_PERIOD = OneWeek
        val PERIODS = listOf(OneDay, OneWeek, OneMonth, OneYear)
    }
}
