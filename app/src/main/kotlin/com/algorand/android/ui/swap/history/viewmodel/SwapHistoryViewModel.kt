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
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.algorand.android.ui.swap.history.mapper.SwapHistoryItemMapper
import com.algorand.android.ui.swap.history.model.SwapHistoryItem
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel.ViewEvent
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel.ViewEvent.DisplayTxnInPeraExplorer
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel.ViewState
import com.algorand.android.ui.swap.tracking.SwapHistoryEventTracker
import com.algorand.android.usecase.NetworkSlugUseCase
import com.algorand.wallet.swap.domain.model.SwapHistoryPagingData
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus.Completed
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus.InProgress
import com.algorand.wallet.swap.domain.usecase.GetSwapHistory
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class SwapHistoryViewModel @Inject constructor(
    private val getSwapHistory: GetSwapHistory,
    private val swapHistoryItemMapper: SwapHistoryItemMapper,
    private val networkSlugUseCase: NetworkSlugUseCase,
    private val swapHistoryEventTracker: SwapHistoryEventTracker,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun init(address: String) {
        stateDelegate.onState<ViewState.Idle> {
            val pagingData = SwapHistoryPagingData(
                address = address,
                statuses = listOf(Completed, InProgress),
                nextUrl = null,
                previousUrl = null
            )
            stateDelegate.updateState {
                ViewState.Content(
                    getSwapHistory(pagingData)
                        .map { pagingData -> pagingData.map { swapHistoryItemMapper(it) } }
                        .cachedIn(viewModelScope)
                        .flowOn(Dispatchers.IO)
                )
            }
        }
    }

    fun displayTxnInPeraExplorer(swapHistoryItem: SwapHistoryItem) {
        viewModelScope.launch {
            swapHistoryEventTracker.logHistoryItem(
                assetInName = swapHistoryItem.assetInShortName.orEmpty(),
                assetOutName = swapHistoryItem.assetOutShortName.orEmpty()
            )
            val viewEvent = DisplayTxnInPeraExplorer(swapHistoryItem.txnGroupId, networkSlugUseCase.getActiveNodeSlug())
            eventDelegate.sendEvent(viewEvent)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val pagingData: Flow<PagingData<SwapHistoryItem>>) : ViewState
    }

    sealed interface ViewEvent {
        data class DisplayTxnInPeraExplorer(val txnGroupId: String?, val nodeSlug: String?) : ViewEvent
    }
}
