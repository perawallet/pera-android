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

package com.algorand.android.ui.asset.remove.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.algorand.android.R
import com.algorand.android.modules.accountsorting.domain.usecase.GetAssetCollectibleLiteSortType
import com.algorand.android.ui.asset.remove.view.RemoveAssetsViewModel.ViewState
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.DescriptionViewItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.SearchViewItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetHeaderItem.TitleViewItem
import com.algorand.android.ui.asset.remove.model.RemoveAssetItemProcessorData
import com.algorand.android.ui.asset.remove.viewmodel.RemoveAssetItemProcessor
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyAssetCanAddressOptOut
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteSortType
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class RemoveAssetsViewModel @Inject constructor(
    private val getAssetCollectibleLiteSortType: GetAssetCollectibleLiteSortType,
    private val stateDelegate: StateDelegate<ViewState>,
    private val isThereAnyAssetCanAddressOptOut: IsThereAnyAssetCanAddressOptOut,
    private val removeAssetItemProcessor: RemoveAssetItemProcessor,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    val accountAddress = savedStateHandle.getOrThrow<String>(PUBLIC_KEY)

    private val assetQueryFlow = MutableStateFlow("")

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initializeViewState() {
        viewModelScope.launch {
            initializeContentState()
            initializeAssetQueryFlow()
        }
    }

    private suspend fun initializeAssetQueryFlow() {
        assetQueryFlow.debounce(QUERY_DEBOUNCE)
            .distinctUntilChanged()
            .flatMapLatest(::getPagedRemoveAssetItemsFlow)
            .collectLatest(::updateContentState)
    }

    private fun updateContentState(pagingData: PagingData<BaseRemoveAssetItem>) {
        stateDelegate.onState<ViewState.Content> { currentState ->
            stateDelegate.updateState {
                currentState.copy(removeAssetItems = pagingData)
            }
        }
    }

    private fun getPagedRemoveAssetItemsFlow(searchKeyword: String): Flow<PagingData<BaseRemoveAssetItem>> {
        val data = getRemoveAssetItemProcessorData(searchKeyword) ?: return emptyFlow()
        return removeAssetItemProcessor.getPagedAssetItems(data)
    }

    private suspend fun initializeContentState() {
        val isThereAnyAssetCanAddressOptOut = isThereAnyAssetCanAddressOptOut(accountAddress)
        val headerItems = getAssetHeaderItems(isThereAnyAssetCanAddressOptOut)
        val sortType = getAssetCollectibleLiteSortType()
        stateDelegate.updateState {
            ViewState.Content(
                sortType = sortType,
                isThereAnyAssetCanAddressOptOut = isThereAnyAssetCanAddressOptOut,
                removeAssetItems = PagingData.from(emptyList()),
                headerItems = headerItems
            )
        }
    }

    private fun getRemoveAssetItemProcessorData(searchKeyword: String): RemoveAssetItemProcessorData? {
        stateDelegate.onState<ViewState.Content> { currentState ->
            return RemoveAssetItemProcessorData(
                address = accountAddress,
                isThereAnyAssetCanAddressOptOut = currentState.isThereAnyAssetCanAddressOptOut,
                sortType = currentState.sortType,
                searchKeyword = searchKeyword
            )
        }
        return null
    }

    fun updateSearchingQuery(query: String) {
        viewModelScope.launch { assetQueryFlow.emit(query) }
    }

    private fun getAssetHeaderItems(isThereAnyAssetCanAddressOptOut: Boolean): List<RemoveAssetHeaderItem> {
        return mutableListOf<RemoveAssetHeaderItem>().apply {
            add(TitleViewItem(R.string.asset_opt_out))
            add(DescriptionViewItem(R.string.to_opt_out_from_an_asset))
            if (isThereAnyAssetCanAddressOptOut) {
                add(SearchViewItem(R.string.search_my_assets))
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val sortType: AssetCollectibleLiteSortType,
            val isThereAnyAssetCanAddressOptOut: Boolean,
            val removeAssetItems: PagingData<BaseRemoveAssetItem>,
            val headerItems: List<RemoveAssetHeaderItem>
        ) : ViewState
    }

    companion object {
        private const val PUBLIC_KEY = "publicKey"
        private const val QUERY_DEBOUNCE = 300L
    }
}
