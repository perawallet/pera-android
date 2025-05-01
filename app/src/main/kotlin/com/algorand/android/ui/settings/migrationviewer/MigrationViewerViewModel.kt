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

package com.algorand.android.ui.settings.migrationviewer

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.Account
import com.algorand.android.modules.settings.domain.usecase.MigrateTo6xUseCase
import com.algorand.android.ui.settings.migrationviewer.MigrationViewerViewModel.ViewEvent
import com.algorand.android.ui.settings.migrationviewer.MigrationViewerViewModel.ViewState
import com.algorand.android.usecase.GetLocalAccountsFromSharedPrefUseCase
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.core.domain.usecase.GetAccountsDetailsFlow
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class MigrationViewerViewModel @Inject constructor(
    private val getAccountsDetailsFlow: GetAccountsDetailsFlow,
    private val migrateTo6xUseCase: MigrateTo6xUseCase,
    private val getLocalAccountsFromSharedPrefUseCase: GetLocalAccountsFromSharedPrefUseCase,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        initViewState()
    }

    fun initViewState() {
        stateDelegate.onState<ViewState.Loading> {
            viewModelScope.launch {
                fetchData()
            }
        }
    }

    private fun fetchData() {
        val oldAccounts = getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref() ?: listOf()

        viewModelScope.launch(Dispatchers.IO) {
            getAccountsDetailsFlow.invoke().collect { newAccounts ->
                stateDelegate.updateState { ViewState.Content(oldAccounts, newAccounts) }
            }
        }
    }

    fun migrate() {
        viewModelScope.launchIO {
            val result = migrateTo6xUseCase.invoke()

            when (result) {
                is PeraResult.Success -> {
                    val migratedCount = result.data
                    if (migratedCount > 0) {
                        fetchData()
                    } else {
                        // No accounts were migrated
                    }
                }

                is PeraResult.Error -> {
                    stateDelegate.updateState {
                        ViewState.Error(
                            error = "Migration failed"
                        )
                    }
                }
            }
        }
    }

    fun triggerEvent(event: ViewEvent) {
        viewModelScope.launch {
            eventDelegate.sendEvent(event)
        }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val error: String) : ViewState
        data class Content(val oldAccounts: List<Account>, val newAccounts: List<AccountDetail>) : ViewState
    }

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
    }
}
