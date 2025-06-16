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

import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewEvent
import com.algorand.android.ui.addressnaming.viewmodel.AddressNamingViewModel.ViewState
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateViewModel

interface AddressNamingViewModel : StateViewModel<ViewState>, EventViewModel<ViewEvent> {

    fun init(address: String)

    fun saveCustomName(name: String)

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val address: String, val currentName: String) : ViewState
    }

    sealed interface ViewEvent {
        data object NavToNextScreen : ViewEvent
    }
}
