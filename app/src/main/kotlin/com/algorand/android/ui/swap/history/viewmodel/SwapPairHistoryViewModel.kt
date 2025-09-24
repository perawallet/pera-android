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

package com.algorand.android.ui.swap.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.swap.history.mapper.SwapPairHistoryItemMapper
import com.algorand.android.ui.swap.history.model.SwapPairHistoryItem
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus.Completed
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus.Failed
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus.Pending
import com.algorand.wallet.swap.domain.usecase.GetSwapPairHistory
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class SwapPairHistoryViewModel @Inject constructor(
    private val getSwapPairHistory: GetSwapPairHistory,
    private val swapPairHistoryItemMapper: SwapPairHistoryItemMapper,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val viewStateFlow = MutableStateFlow<ViewState>(ViewState.Idle)

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun init(address: Flow<String?>, swapWidgetViewState: Flow<SwapWidgetViewModel.ViewState>) {
        stateDelegate.onState<ViewState.Idle> {
            initSwapPairHistory(address)
            combine(swapWidgetViewState, viewStateFlow) { widgetState, viewState ->
                val widgetContentState = (widgetState as? SwapWidgetViewModel.ViewState.Content)?.contentState
                if (widgetContentState is SwapWidgetViewModel.ViewState.Content.ContentState.Idle) {
                    stateDelegate.updateState { viewState }
                } else {
                    stateDelegate.updateState { ViewState.Idle }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun initSwapPairHistory(addressFlow: Flow<String?>) {
        addressFlow.onEach { address ->
            viewStateFlow.value = ViewState.Loading
            if (address.isNullOrBlank()) return@onEach
            viewStateFlow.value = getSwapPairHistory(address, listOf(Completed, Failed, Pending)).use(
                onSuccess = { pairs ->
                    if (pairs.isEmpty()) {
                        ViewState.Empty
                    } else {
                        val items = pairs.map { swapPairHistoryItemMapper(it) }
                        ViewState.Content(items)
                    }
                },
                onFailed = { _, _ ->
                    ViewState.Error
                }
            )
        }.launchIn(viewModelScope)
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data object Error : ViewState
        data object Empty : ViewState
        data class Content(val pairs: List<SwapPairHistoryItem>) : ViewState
    }
}
