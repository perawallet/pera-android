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

package com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.addaccount.joint.creation.mapper.SelectedJointAccountMapper
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.usecase.AddJointAccountSelectionUseCase
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContact
import com.algorand.wallet.jointaccount.domain.usecase.CheckIsJointAccount
import com.algorand.android.utils.isValidAddress
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AddJointAccountViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val addJointAccountSelectionUseCase: AddJointAccountSelectionUseCase,
    private val createExternalAddressAsContact: CreateExternalAddressAsContact,
    private val selectedJointAccountMapper: SelectedJointAccountMapper,
    private val checkIsJointAccount: CheckIsJointAccount
) : ViewModel(),
    StateViewModel<AddJointAccountViewModel.ViewState> by stateDelegate,
    EventViewModel<AddJointAccountViewModel.ViewEvent> by eventDelegate {

    private val searchQueryFlow = MutableStateFlow("")

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        initSearchQueryFlow()
    }

    private fun initSearchQueryFlow() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(SEARCH_DEBOUNCE_DELAY_MS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    stateDelegate.updateState { currentState ->
                        val content = currentState as? ViewState.Content ?: ViewState.Content()
                        content.copy(
                            searchQuery = query,
                            showEmptyState = content.accountList.isEmpty() && query.isNotEmpty()
                        )
                    }
                    val list = addJointAccountSelectionUseCase.getAccountSelectionList(query)
                    stateDelegate.updateState { currentState ->
                        val content = currentState as? ViewState.Content ?: ViewState.Content()
                        content.copy(accountList = list).withCategorizedLists()
                    }
                }
        }
    }

    fun onSearchQueryUpdate(query: String) {
        searchQueryFlow.value = query
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: ViewState.Content()
            content.copy(
                searchQuery = query,
                showEmptyState = content.accountList.isEmpty() && query.isNotEmpty()
            )
        }
    }

    fun resetSearchQuery() {
        searchQueryFlow.value = ""
    }

    fun updateClipboardAddress(copiedText: String?) {
        val validAddress = copiedText?.takeIf { it.isValidAddress() }
        stateDelegate.updateState { currentState ->
            when (currentState) {
                is ViewState.Loading -> ViewState.Content(clipboardAddress = validAddress)
                is ViewState.Content -> currentState.copy(clipboardAddress = validAddress)
            }
        }
    }

    fun createSelectedAccountFromItem(address: String): SelectedJointAccountItem? {
        val content = state.value as? ViewState.Content ?: return selectedJointAccountMapper.mapFromSelectionList(
            address,
            emptyList()
        )
        return selectedJointAccountMapper.mapFromSelectionList(address, content.accountList)
    }

    fun onAccountSelected(address: String) {
        val selectedAccount = createSelectedAccountFromItem(address)
        if (selectedAccount == null) {
            viewModelScope.launch {
                eventDelegate.sendEvent(ViewEvent.ShowError)
            }
            return
        }
        viewModelScope.launch {
            setCheckingJointAccount(true)
            checkIsJointAccount(listOf(address)).use(
                onSuccess = { results ->
                    val isJointAccount = results.any { it.isJointAccount }
                    setCheckingJointAccount(false)
                    if (isJointAccount) {
                        eventDelegate.sendEvent(ViewEvent.ShowJointAccountError)
                    } else {
                        eventDelegate.sendEvent(ViewEvent.NavigateBackWithSelectedAccount(selectedAccount))
                    }
                },
                onFailed = { _, _ ->
                    setCheckingJointAccount(false)
                    eventDelegate.sendEvent(ViewEvent.ShowError)
                }
            )
        }
    }

    fun onExternalAddressSelected(address: String) {
        viewModelScope.launch {
            setCheckingJointAccount(true)
            checkIsJointAccount(listOf(address)).use(
                onSuccess = { results ->
                    val isJointAccount = results.any { it.isJointAccount }
                    setCheckingJointAccount(false)
                    if (isJointAccount) {
                        eventDelegate.sendEvent(ViewEvent.ShowJointAccountError)
                    } else {
                        val selectedAccount = createSelectedAccountFromExternalAddress(address)
                        if (selectedAccount != null) {
                            eventDelegate.sendEvent(ViewEvent.NavigateBackWithSelectedAccount(selectedAccount))
                        } else {
                            eventDelegate.sendEvent(ViewEvent.ShowError)
                        }
                    }
                },
                onFailed = { _, _ ->
                    setCheckingJointAccount(false)
                    eventDelegate.sendEvent(ViewEvent.ShowError)
                }
            )
        }
    }

    fun onNfdSelected(address: String) {
        viewModelScope.launch {
            setCheckingJointAccount(true)
            checkIsJointAccount(listOf(address)).use(
                onSuccess = { results ->
                    val isJointAccount = results.any { it.isJointAccount }
                    setCheckingJointAccount(false)
                    if (isJointAccount) {
                        eventDelegate.sendEvent(ViewEvent.ShowJointAccountError)
                    } else {
                        val selectedAccount = createSelectedAccountFromNfd(address)
                        if (selectedAccount != null) {
                            eventDelegate.sendEvent(ViewEvent.NavigateBackWithSelectedAccount(selectedAccount))
                        } else {
                            eventDelegate.sendEvent(ViewEvent.ShowError)
                        }
                    }
                },
                onFailed = { _, _ ->
                    setCheckingJointAccount(false)
                    eventDelegate.sendEvent(ViewEvent.ShowError)
                }
            )
        }
    }

    private fun setCheckingJointAccount(isChecking: Boolean) {
        stateDelegate.updateState { currentState ->
            when (currentState) {
                is ViewState.Loading -> currentState
                is ViewState.Content -> currentState.copy(isCheckingJointAccount = isChecking)
            }
        }
    }

    private suspend fun createSelectedAccountFromExternalAddress(address: String): SelectedJointAccountItem? {
        return createExternalAddressAsContact(address)
    }

    private suspend fun createSelectedAccountFromNfd(address: String): SelectedJointAccountItem? {
        val content = state.value as? ViewState.Content ?: return null
        val nfdItem = content.accountList.filterIsInstance<JointAccountSelectionListItem.NfdItem>()
            .find { it.address == address } ?: return null
        return createExternalAddressAsContact(address, displayName = nfdItem.domainName)
    }

    sealed interface ViewState {
        data object Loading : ViewState

        data class Content(
            val searchQuery: String = "",
            val accountList: List<JointAccountSelectionListItem> = emptyList(),
            val externalAddresses: List<JointAccountSelectionListItem.ExternalAddressItem> = emptyList(),
            val accounts: List<JointAccountSelectionListItem.AccountItem> = emptyList(),
            val contacts: List<JointAccountSelectionListItem.ContactItem> = emptyList(),
            val nfds: List<JointAccountSelectionListItem.NfdItem> = emptyList(),
            val clipboardAddress: String? = null,
            val showEmptyState: Boolean = false,
            val isCheckingJointAccount: Boolean = false
        ) : ViewState {
            val hasResults: Boolean get() = accountList.isNotEmpty()

            fun withCategorizedLists(): Content = copy(
                externalAddresses = accountList.filterIsInstance<JointAccountSelectionListItem.ExternalAddressItem>(),
                accounts = accountList.filterIsInstance<JointAccountSelectionListItem.AccountItem>(),
                contacts = accountList.filterIsInstance<JointAccountSelectionListItem.ContactItem>(),
                nfds = accountList.filterIsInstance<JointAccountSelectionListItem.NfdItem>(),
                showEmptyState = accountList.isEmpty() && searchQuery.isNotEmpty()
            )
        }
    }

    sealed interface ViewEvent {
        data class NavigateBackWithSelectedAccount(val account: SelectedJointAccountItem) : ViewEvent
        data object ShowError : ViewEvent
        data object ShowJointAccountError : ViewEvent
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }
}
