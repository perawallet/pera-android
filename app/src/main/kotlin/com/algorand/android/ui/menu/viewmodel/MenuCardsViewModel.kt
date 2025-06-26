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

package com.algorand.android.ui.menu.viewmodel

import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState
import com.algorand.wallet.viewmodel.StateViewModel

interface MenuCardsViewModel : StateViewModel<ViewState> {

    fun initCardState()

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data object Waitlisted : ViewState
        data object NewUser : ViewState
        data object CardCreated : ViewState
        data object Error : ViewState
    }
}
