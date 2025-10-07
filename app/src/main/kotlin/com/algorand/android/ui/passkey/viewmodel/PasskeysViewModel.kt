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

package com.algorand.android.ui.passkey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.credentials.passkeys.domain.model.Passkey
import com.algorand.android.credentials.passkeys.domain.usecase.GetAllPasskeysAsFlow
import com.algorand.android.credentials.passkeys.domain.usecase.RemovePasskeyByCredentialId
import com.algorand.android.ui.passkey.model.PasskeyListItem
import com.algorand.android.ui.passkey.viewmodel.PasskeysViewModel.ViewState
import com.algorand.wallet.utils.date.RelativeTimeDifference
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class PasskeysViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAllPasskeysAsFlow: GetAllPasskeysAsFlow,
    private val removePasskeyByCredentialId: RemovePasskeyByCredentialId,
    private val relativeTimeDifference: RelativeTimeDifference
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initPasskeys() {
        stateDelegate.onState<ViewState.Idle> {
            getAllPasskeysAsFlow()
                .map(::mapToPasskeyListItems)
                .onEach(::updateViewState)
                .launchIn(viewModelScope)
        }
    }

    fun removePasskey(item: PasskeyListItem) {
        viewModelScope.launch {
            removePasskeyByCredentialId(item.credId)
        }
    }

    private fun mapToPasskeyListItems(passkeys: List<Passkey>): List<PasskeyListItem> {
        return passkeys.map { passkey ->
            with(passkey) {
                PasskeyListItem(
                    credId = credId,
                    rpId = site.url,
                    displayName = site.name,
                    username = username,
                    lastUsedRelativeTime = lastUsed?.let { relativeTimeDifference.getCurrentRelativeTime(it) }
                )
            }
        }
    }

    private fun updateViewState(passkeyListItems: List<PasskeyListItem>) {
        stateDelegate.updateState {
            if (passkeyListItems.isEmpty()) ViewState.Empty else ViewState.Content(passkeyListItems)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Empty : ViewState
        data class Content(val passkeyListItems: List<PasskeyListItem>) : ViewState
    }
}
