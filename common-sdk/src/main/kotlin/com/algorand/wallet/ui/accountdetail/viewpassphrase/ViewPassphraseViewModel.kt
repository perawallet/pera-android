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

package com.algorand.wallet.ui.accountdetail.viewpassphrase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.wallet.account.local.domain.model.AccountMnemonic
import com.algorand.wallet.account.local.domain.usecase.GetAccountMnemonic
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewEvent
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState.Idle
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ViewPassphraseViewModel @Inject constructor(
    private val getAccountMnemonic: GetAccountMnemonic,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun initViewState(accountAddress: String) {
        stateDelegate.onState<Idle> {
            stateDelegate.updateState { ViewState.Loading }
            viewModelScope.launch {
                getAccountMnemonic(accountAddress).use(
                    onSuccess = ::displayMnemonic,
                    onFailed = { _, _ -> displayError() }
                )
            }
        }
    }

    private fun displayMnemonic(mnemonic: AccountMnemonic) {
        stateDelegate.updateState {
            ViewState.Content(mnemonic.words)
        }
    }

    private fun displayError() {
        viewModelScope.launch {
            eventDelegate.sendEvent(ViewEvent.ShowGenericError)
            eventDelegate.sendEvent(ViewEvent.NavigateBack)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data class Content(val mnemonicWords: List<String>) : ViewState
    }

    sealed interface ViewEvent {
        data object ShowGenericError : ViewEvent
        data object NavigateBack : ViewEvent
    }
}
