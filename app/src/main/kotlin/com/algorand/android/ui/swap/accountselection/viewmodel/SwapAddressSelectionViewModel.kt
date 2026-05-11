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

package com.algorand.android.ui.swap.accountselection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.android.ui.swap.accountselection.viewmodel.SwapAddressSelectionViewModel.ViewState
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwapAddressSelectionViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountLiteCacheData: GetAccountLiteCacheData,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState() {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                val authAccountLites = getAccountLiteCacheData()?.accountLites?.values
                    ?.filter {
                        val accountType = it.cachedInfo?.type ?: return@filter false
                        accountType.canSignTransaction()
                    }
                    .orEmpty()
                val addresses = authAccountLites.map { accountLite ->
                    getAccountDisplayName(accountLite) to getAccountIconDrawablePreview(accountLite)
                }
                stateDelegate.updateState { ViewState.Content(addresses) }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val addresses: List<Pair<AccountDisplayName, AccountIconDrawablePreview>>) : ViewState
    }
}
