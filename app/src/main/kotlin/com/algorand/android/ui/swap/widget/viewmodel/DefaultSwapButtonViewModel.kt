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
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel.ViewState.Invisible
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel.ViewState.Visible
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail.SwapQuoteState.Swappable
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private typealias WidgetQuoteState = SwapWidgetViewModel.ViewState.Content.ContentState.Quote
private typealias WidgetErrorState = SwapWidgetViewModel.ViewState.Content.ContentState.Error
private typealias WidgetIdleState = SwapWidgetViewModel.ViewState.Content.ContentState.Idle

@HiltViewModel
class DefaultSwapButtonViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<SwapButtonViewModel.ViewState>
) : ViewModel(), SwapButtonViewModel {

    init {
        stateDelegate.setDefaultState(Invisible)
    }

    override val state: StateFlow<SwapButtonViewModel.ViewState>
        get() = stateDelegate.state

    override fun init(widgetViewState: Flow<SwapWidgetViewModel.ViewState>) {
        widgetViewState
            .onEach(::updateViewState)
            .launchIn(viewModelScope)
    }

    private fun updateViewState(widgetViewState: SwapWidgetViewModel.ViewState) {
        val viewState = when (widgetViewState) {
            is SwapWidgetViewModel.ViewState.Content -> {
                when (widgetViewState.contentState) {
                    is WidgetErrorState -> Visible(isEnabled = false)
                    is WidgetIdleState -> Invisible
                    is WidgetQuoteState -> {
                        val isEnabled = widgetViewState.contentState.selectedQuoteDetail.state is Swappable
                        Visible(isEnabled, widgetViewState.contentState.selectedQuoteDetail.quote)
                    }
                }
            }

            is SwapWidgetViewModel.ViewState.Loading -> Visible(isEnabled = false)
        }
        stateDelegate.updateState { viewState }
    }
}
