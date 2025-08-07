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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel.ViewState.Content
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class DefaultSwapProviderWidgetViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<SwapProviderWidgetViewModel.ViewState>
) : ViewModel(), SwapProviderWidgetViewModel {

    init {
        stateDelegate.setDefaultState(SwapProviderWidgetViewModel.ViewState.Idle)
    }

    override val state: StateFlow<SwapProviderWidgetViewModel.ViewState>
        get() = stateDelegate.state

    override fun init(swapWidgetViewState: Flow<SwapWidgetViewModel.ViewState>) {
        swapWidgetViewState
            .onEach(::updateViewState)
            .launchIn(viewModelScope)
    }

    private fun updateViewState(widgetViewState: SwapWidgetViewModel.ViewState) {
        val widgetContentState = (widgetViewState as? SwapWidgetViewModel.ViewState.Content)?.contentState
        val viewState = if (widgetContentState is SwapWidgetViewModel.ViewState.Content.ContentState.Quote) {
            getContentState(widgetContentState)
        } else {
            SwapProviderWidgetViewModel.ViewState.Idle
        }
        stateDelegate.updateState { viewState }
    }

    private fun getContentState(quoteState: SwapWidgetViewModel.ViewState.Content.ContentState.Quote): Content {
        val selectedQuote = quoteState.selectedQuoteDetail.quote
        return Content(
            providerName = selectedQuote.provider.name,
            assetInShortName = selectedQuote.assetInDetail.shortName.orEmpty(),
            assetOutShortName = selectedQuote.assetOutDetail.shortName.orEmpty(),
            unitPrice = selectedQuote.price.toString(),
            isBestOfferSelected = quoteState.selectedQuoteId == quoteState.bestOfferQuoteId,
            hasMultipleProvider = quoteState.quotes.size > 1
        )
    }
}
