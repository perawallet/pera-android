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

package com.algorand.android.modules.rekey.previouslyrekeyedaccountconfirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.rekey.previouslyrekeyedaccountconfirmation.ui.RekeyedAccountRekeyConfirmationViewModel.ViewState
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RekeyedAccountRekeyConfirmationViewModel @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val stateDelegate: StateDelegate<ViewState>,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val navArgs = RekeyedAccountRekeyConfirmationBottomSheetArgs.fromSavedStateHandle(savedStateHandle)
    private val accountAddress: String = navArgs.accountAddress
    private val authAccountAddress: String = navArgs.authAccountAddress

    init {
        initViewState()
    }

    private fun initViewState() {
        stateDelegate.setDefaultState(ViewState("", ""))
        viewModelScope.launch {
            val viewState = ViewState(
                accountDisplayName = getAccountDisplayName(accountAddress).primaryDisplayName,
                authAccountDisplayName = getAccountDisplayName(authAccountAddress).primaryDisplayName
            )
            stateDelegate.updateState { viewState }
        }
    }

    data class ViewState(
        val accountDisplayName: String,
        val authAccountDisplayName: String
    )
}
