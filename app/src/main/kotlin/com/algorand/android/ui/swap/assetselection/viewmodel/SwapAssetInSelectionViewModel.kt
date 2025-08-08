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

@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.algorand.android.ui.swap.assetselection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.algorand.android.ui.asset.lite.usecase.GetPaginatedAssetListItems
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.swap.assetselection.viewmodel.SwapAssetInSelectionViewModel.ViewState
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.FilterOutZeroAmount
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter.SearchKeyword
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType.NameAscending
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class SwapAssetInSelectionViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getPaginatedAssetListItems: GetPaginatedAssetListItems
) : ViewModel(), StateViewModel<ViewState> {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    private val queryFlow = MutableStateFlow<String>("")
    private val addressFlow = MutableStateFlow<String?>(null)

    private val assetPagingItems: Flow<PagingData<AssetListItem>> =
        combine(addressFlow.filterNotNull(), queryFlow) { address, query ->
            AssetCollectibleLiteQuery(
                address,
                sortType = NameAscending,
                filters = listOf(FilterOutZeroAmount, SearchKeyword(query))
            )
        }.flatMapLatest { assetQuery ->
            getPaginatedAssetListItems(assetQuery)
        }.cachedIn(viewModelScope)

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    fun init(address: String) {
        stateDelegate.onState<ViewState.Idle> {
            addressFlow.value = address
            stateDelegate.updateState { ViewState.Content(assetPagingItems) }
        }
    }

    fun updateQuery(query: String) {
        queryFlow.value = query
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(val assetList: Flow<PagingData<AssetListItem>>) : ViewState
    }
}
