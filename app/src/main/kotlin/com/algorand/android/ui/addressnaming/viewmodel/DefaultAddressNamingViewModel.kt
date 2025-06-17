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

package com.algorand.android.ui.addressnaming.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewEvent
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewState
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewState.Content
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DefaultAddressNamingViewModel @Inject constructor(
    private val setAccountCustomName: SetAccountCustomName,
    private val getAccountCustomName: GetAccountCustomName,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), AddressNamingViewModel {

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    override val viewEvent: Flow<ViewEvent>
        get() = eventDelegate.viewEvent

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override fun init(address: String) {
        viewModelScope.launch {
            val currentName = getAccountCustomName(address) ?: address.toShortenedAddress()
            stateDelegate.updateState { Content(address, currentName) }
        }
    }

    override fun saveCustomName(name: String) {
        stateDelegate.onState<Content> { contentState ->
            viewModelScope.launch {
                setAccountCustomName(contentState.address, name)
                eventDelegate.sendEvent(ViewEvent.NavToNextScreen)
            }
        }
    }
}
