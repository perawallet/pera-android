/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assets.manage.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.assets.manage.ui.ManageAssetsViewModel.ViewState
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ManageAssetsViewModel @Inject constructor(
    private val getAccountType: GetAccountType,
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val navArgs = ManageAssetsBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    val publicKey = navArgs.publicKey

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState() {
        viewModelScope.launch {
            val canAccountSignTransaction = getAccountType(publicKey)?.canSignTransaction() == true
            stateDelegate.updateState {
                ViewState.Content(canAccountSignTransaction)
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val canAccountSignTransaction: Boolean) : ViewState
    }
}
