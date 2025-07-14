/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.accountdetail.assets.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountdetail.assets.ui.AccountAssetsFragment.Companion.ADDRESS_KEY
import com.algorand.android.modules.accountdetail.assets.ui.domain.AccountDetailAccountsItemProcessor
import com.algorand.android.modules.accountdetail.assets.ui.domain.AccountDetailAssetsItemProcessor
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountAssetsPreview
import com.algorand.android.ui.accountdetail.assets.tracker.AccountAssetsEventTracker
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.privacy.domain.usecase.TogglePrivacyMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AccountAssetsViewModel @Inject constructor(
    private val accountsItemProcessor: AccountDetailAccountsItemProcessor,
    private val assetsItemProcessor: AccountDetailAssetsItemProcessor,
    private val togglePrivacyMode: TogglePrivacyMode,
    private val accountAssetsEventTracker: AccountAssetsEventTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel(), AccountAssetsEventTracker by accountAssetsEventTracker {

    private val accountAddress: String = savedStateHandle.getOrThrow(ADDRESS_KEY)

    val accountAssetsFlow: StateFlow<AccountAssetsPreview?> get() = _accountAssetsFlow
    private val _accountAssetsFlow = MutableStateFlow<AccountAssetsPreview?>(null)

    val isWatchAccount: Boolean?
        get() = _accountAssetsFlow.value?.isWatchAccount

    private val searchQueryFlow = MutableStateFlow("")

    private var searchQueryFlowJob: Job? = null

    fun initAccountAssetsFlow() {
        searchQueryFlowJob?.cancel()
        searchQueryFlowJob = viewModelScope.launch(Dispatchers.IO) {
            searchQueryFlow
                .debounce(QUERY_DEBOUNCE)
                .distinctUntilChanged()
                .flatMapLatest(::initAccountDetail)
                .collectLatest { list -> _accountAssetsFlow.emit(list) }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun togglePrivacy() {
        viewModelScope.launch {
            togglePrivacyMode()
        }
    }

    private suspend fun initAccountDetail(query: String): Flow<AccountAssetsPreview> {
        return combine(
            accountsItemProcessor.getAccountDetailsItemsFlow(accountAddress, query),
            assetsItemProcessor.getAssetsPagingFlow(viewModelScope, accountAddress, query)
        ) { accountDetailItems, assetPagingItems ->
            AccountAssetsPreview(assetPagingItems, accountDetailItems, false)
        }
    }

    companion object {
        private const val QUERY_DEBOUNCE = 400L
    }
}
