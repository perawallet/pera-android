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

package com.algorand.android.ui.rekeyedaccounts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.AccountCreation
import com.algorand.android.ui.rekeyedaccounts.mapper.RekeyedAccountSelectionItemMapper
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionItem.AccountItem
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewEvent
import com.algorand.android.ui.rekeyedaccounts.viewmodel.RekeyedAccountSelectionViewModel.ViewState
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.IsAccountLimitExceedUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RekeyedAccountSelectionViewModel @Inject constructor(
    private val rekeyedAccountSelectionItemMapper: RekeyedAccountSelectionItemMapper,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val isAccountLimitExceedUseCase: IsAccountLimitExceedUseCase,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initializeViewState(args: List<RekeyedAccountSelectionNavArg>) {
        viewModelScope.launch {
            val selectionItems = rekeyedAccountSelectionItemMapper(args)
            stateDelegate.updateState {
                ViewState.Content(selectionItems)
            }
        }
    }

    fun selectAccount(accountItem: AccountItem) {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val updatedState = contentState.selectionItems.map { item ->
                if (item.isSelectedItem(accountItem)) {
                    (item as AccountItem).copy(isSelected = !item.isSelected)
                } else {
                    item
                }
            }
            stateDelegate.updateState {
                contentState.copy(selectionItems = updatedState)
            }
        }
    }

    fun addSelectedAccounts() {
        viewModelScope.launch {
            val selectedAccounts = getSelectedAccounts()
            if (selectedAccounts.isEmpty()) return@launch
            selectedAccounts.forEach { item ->
                if (isAccountLimitExceedUseCase.isAccountLimitExceed()) {
                    eventDelegate.sendEvent(ViewEvent.ShowAccountLimitError)
                    return@launch
                }
                addAccount(item)
            }
            eventDelegate.sendEvent(ViewEvent.NavigateToNextScreen)
        }
    }

    private suspend fun addAccount(accountItem: AccountItem) {
        val rekeyedAccount = AccountCreation(
            address = accountItem.accountDisplayName.accountAddress,
            customName = accountItem.accountDisplayName.primaryDisplayName,
            isBackedUp = true,
            type = AccountCreation.Type.NoAuth,
            creationType = CreationType.REKEYED
        )
        accountAdditionUseCase.addNewAccount(rekeyedAccount)
    }

    private fun getSelectedAccounts(): List<AccountItem> {
        return (stateDelegate.state.value as? ViewState.Content)?.selectionItems
            ?.filterIsInstance<AccountItem>()
            ?.filter { it.isSelected }
            .orEmpty()
    }

    private fun RekeyedAccountSelectionItem.isSelectedItem(accountItem: AccountItem): Boolean {
        return this is AccountItem &&
            authAddress == accountItem.authAddress &&
            accountDisplayName.accountAddress == accountItem.accountDisplayName.accountAddress
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val selectionItems: List<RekeyedAccountSelectionItem>) : ViewState {
            val isAddButtonEnabled: Boolean
                get() = selectionItems.any { it is AccountItem && it.isSelected }
        }
    }

    sealed interface ViewEvent {
        data object NavigateToNextScreen : ViewEvent
        data object ShowAccountLimitError : ViewEvent
    }
}
