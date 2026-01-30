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
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddJointAccountViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val addJointAccountSelectionUseCase: AddJointAccountSelectionUseCase,
    private val createExternalAddressAsContact: CreateExternalAddressAsContact,
    private val selectedJointAccountMapper: SelectedJointAccountMapper
) : ViewModel(),
    StateViewModel<AddJointAccountViewModel.ViewState> by stateDelegate {

    private var searchJob: Job? = null

    init {
        stateDelegate.setDefaultState(ViewState())
        loadAccountList()
    }

    fun onSearchQueryUpdate(query: String) {
        stateDelegate.updateState { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MS)
            loadAccountList()
        }
    }

    fun resetSearchQuery() {
        searchJob?.cancel()
        stateDelegate.updateState { it.copy(searchQuery = "") }
        loadAccountList()
    }

    fun createSelectedAccountFromItem(address: String): SelectedJointAccountItem? {
        return selectedJointAccountMapper.mapFromSelectionList(address, state.value.accountList)
    }

    suspend fun createSelectedAccountFromExternalAddress(address: String): SelectedJointAccountItem? {
        val currentList = state.value.accountList
        val externalItem = currentList.filterIsInstance<JointAccountSelectionListItem.ExternalAddressItem>()
            .find { it.address == address }

        val shortenedAddress = externalItem?.shortenedAddress ?: address.toShortenedAddress()

        return createExternalAddressAsContact(address, shortenedAddress)
    }

    private fun loadAccountList() {
        viewModelScope.launch {
            addJointAccountSelectionUseCase.getAccountSelectionList(
                query = state.value.searchQuery,
            ).collectLatest { list ->
                stateDelegate.updateState { it.copy(accountList = list).withCategorizedLists() }
            }
        }
    }

    data class ViewState(
        val searchQuery: String = "",
        val accountList: List<JointAccountSelectionListItem> = emptyList(),
        val externalAddresses: List<JointAccountSelectionListItem.ExternalAddressItem> = emptyList(),
        val accounts: List<JointAccountSelectionListItem.AccountItem> = emptyList(),
        val contacts: List<JointAccountSelectionListItem.ContactItem> = emptyList(),
        val nfds: List<JointAccountSelectionListItem.NfdItem> = emptyList()
    ) {
        val hasResults: Boolean get() = accountList.isNotEmpty()

        fun withCategorizedLists(): ViewState = copy(
            externalAddresses = accountList.filterIsInstance<JointAccountSelectionListItem.ExternalAddressItem>(),
            accounts = accountList.filterIsInstance<JointAccountSelectionListItem.AccountItem>(),
            contacts = accountList.filterIsInstance<JointAccountSelectionListItem.ContactItem>(),
            nfds = accountList.filterIsInstance<JointAccountSelectionListItem.NfdItem>()
        )
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }
}
