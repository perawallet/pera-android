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

package com.algorand.android.ui.swap.widget.viewmodel

import com.algorand.wallet.viewmodel.StateViewModel
import kotlinx.coroutines.flow.Flow

interface SwapProviderWidgetViewModel : StateViewModel<SwapProviderWidgetViewModel.ViewState> {

    fun init(swapWidgetViewState: Flow<SwapWidgetViewModel.ViewState>)

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val providerName: String,
            val assetInShortName: String,
            val assetOutShortName: String,
            val unitPrice: String,
            val isBestOfferSelected: Boolean,
            val hasMultipleProvider: Boolean
        ) : ViewState
    }
}
