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

@file:OptIn(FlowPreview::class)

package com.algorand.android.ui.swap.assetselection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.asset.lite.mapper.AssetListItemMapper
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetOutSelectionViewModel.ViewEvent
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetOutSelectionViewModel.ViewState
import com.algorand.wallet.account.lite.domain.usecase.GetAssetHoldingsLite
import com.algorand.wallet.asset.domain.usecase.GetAssetFavoriteStatuses
import com.algorand.wallet.swap.domain.usecase.GetAvailableSwapAssets
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class SwapAssetOutSelectionViewModel @Inject constructor(
    private val getAvailableSwapAssets: GetAvailableSwapAssets,
    private val getAssetHoldingsLite: GetAssetHoldingsLite,
    private val getAssetFavoriteStatuses: GetAssetFavoriteStatuses,
    private val assetListItemMapper: AssetListItemMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val queryFlow = MutableStateFlow<String?>(null)

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun init(address: String, assetInId: Long) {
        queryFlow
            .debounce(QUERY_DEBOUNCE_TIME)
            .onEach { query ->
                stateDelegate.updateState { ViewState.Loading }
                getAvailableSwapAssets(assetInId, query).use(
                    onSuccess = { availableAssets ->
                        val assetHoldings = getAssetHoldingsLite(address, availableAssets.map { it.assetId })
                        val assetFavoriteStatuses = getAssetFavoriteStatuses(availableAssets.map { it.assetId })
                        val assetListItems = availableAssets.map { availableAsset ->
                            val isFavorite = assetFavoriteStatuses[availableAsset.assetId]
                            assetListItemMapper(assetHoldings, availableAsset, isFavorite)
                        }
                        stateDelegate.updateState { ViewState.Content(assetListItems) }
                    },
                    onFailed = { _, _ ->
                        eventDelegate.sendEvent(ViewEvent.ShowGenericError)
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    fun updateQuery(query: String) {
        queryFlow.value = query
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data class Content(val assetList: List<AssetListItem>) : ViewState
    }

    sealed interface ViewEvent {
        data object ShowGenericError : ViewEvent
    }

    private companion object {
        const val QUERY_DEBOUNCE_TIME = 400L
    }
}
