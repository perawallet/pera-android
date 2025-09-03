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

package com.algorand.android.ui.swap.providers.viewmodel

import androidx.lifecycle.ViewModel
import com.algorand.android.ui.swap.providers.mapper.SwapQuoteProviderSelectionItemMapper
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState.Quote.QuoteSelection
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SwapQuoteProvidersViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val selectionItemMapper: SwapQuoteProviderSelectionItemMapper
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initProviders(quoteSelection: QuoteSelection, swapQuotes: List<SwapQuoteV2>) {
        val providerItems = swapQuotes.map { swapQuote -> selectionItemMapper(swapQuote) }
        val selectedItem = if (quoteSelection.selectionType is QuoteSelection.Type.Auto) {
            SwapQuoteProviderSelectionItem.Auto
        } else {
            providerItems.firstOrNull { it.quoteId == quoteSelection.quoteId } ?: SwapQuoteProviderSelectionItem.Auto
        }
        val listItems: List<SwapQuoteProviderSelectionItem> = buildList {
            add(SwapQuoteProviderSelectionItem.Auto)
            addAll(providerItems)
        }
        stateDelegate.updateState {
            ViewState.Content(selectedItem, listItems)
        }
    }

    fun setSelectedItem(selectedItem: SwapQuoteProviderSelectionItem) {
        stateDelegate.onState<ViewState.Content> { content ->
            stateDelegate.updateState {
                content.copy(selectedItem = selectedItem)
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val selectedItem: SwapQuoteProviderSelectionItem,
            val items: List<SwapQuoteProviderSelectionItem>
        ) : ViewState
    }
}
