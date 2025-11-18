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

@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.algorand.android.ui.asset.collectible.listing.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.CurrencyCachingError
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Data
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.EmptyLocalAccounts
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Idle
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Loading
import com.algorand.android.modules.collectibles.filter.domain.usecase.ClearCollectibleFiltersPreferencesUseCase
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.collectibles.listingviewtype.domain.usecase.GetNFTListingViewTypePreferenceUseCase
import com.algorand.android.modules.collectibles.listingviewtype.domain.usecase.SaveNFTListingViewTypePreferenceUseCase
import com.algorand.android.modules.tracking.nft.CollectibleEventTracker
import com.algorand.android.ui.asset.collectible.listing.mapper.CollectibleListItemMapper
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItemProviderData
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Content
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Empty
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Empty.AllFilteredOut
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel.ViewState.ContentState.ContentStateType.Error
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.asset.collectible.domain.model.FilteredCollectibleCount
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.viewmodel.StateDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class CollectibleListingViewModelDelegate @Inject constructor(
    private val collectibleListingPreviewUseCase: CollectibleListingPreviewUseCase,
    private val saveNFTListingViewTypePreferenceUseCase: SaveNFTListingViewTypePreferenceUseCase,
    private val getNFTListingViewType: GetNFTListingViewTypePreferenceUseCase,
    private val collectibleEventTracker: CollectibleEventTracker,
    private val collectibleListItemMapper: CollectibleListItemMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val clearCollectibleFiltersPreferences: ClearCollectibleFiltersPreferencesUseCase
) : CollectibleListingViewModel {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    private var coroutineScope: CoroutineScope? = null
    private var collectCollectibleListingPreviewJob: Job? = null

    private val nftListingTypeFlow = MutableStateFlow(NFTListingViewType.DEFAULT_VIEW_TYPE)
    private val searchKeywordFlow = MutableStateFlow("")
    private lateinit var accountLiteCacheStatusFlow: Flow<AccountLiteCacheStatus>
    private lateinit var headerItemProvider: BaseCollectibleListHeaderItemProvider

    fun init(
        scope: CoroutineScope,
        accountLiteFlow: Flow<AccountLiteCacheStatus>,
        headerItemProvider: BaseCollectibleListHeaderItemProvider
    ) {
        coroutineScope = scope
        accountLiteCacheStatusFlow = accountLiteFlow
        this.headerItemProvider = headerItemProvider
        initPreviewFlow()
    }

    fun clearResources() {
        collectCollectibleListingPreviewJob?.cancel()
        collectCollectibleListingPreviewJob = null
        coroutineScope = null
    }

    override fun clearFilters() {
        coroutineScope?.launch {
            clearCollectibleFiltersPreferences()
        }?.invokeOnCompletion {
            initPreviewFlow()
        }
    }

    private fun initPreviewFlow() {
        if (collectCollectibleListingPreviewJob?.isActive == true) {
            collectCollectibleListingPreviewJob?.cancel()
        }
        collectCollectibleListingPreviewJob = coroutineScope?.launch(Dispatchers.IO) {
            nftListingTypeFlow.value = getNFTListingViewType()
            accountLiteCacheStatusFlow
                .distinctUntilChanged()
                .flatMapLatest(::getViewStateFlow)
                .collectLatest { state -> stateDelegate.updateState { state } }
        }
    }

    private fun getViewStateFlow(cacheStatus: AccountLiteCacheStatus): Flow<ViewState> {
        return when (cacheStatus) {
            Idle, Loading -> flowOf(ViewState.Loading)
            EmptyLocalAccounts -> flowOf(ViewState.ContentState(isThereAnyAuthAddress = false, Empty.NoCollectible))
            is Data -> initSearchKeywordFlow(cacheStatus.accountLites)
            is CurrencyCachingError -> flowOf(ViewState.ContentState(isThereAnyAuthAddress = false, Error))
        }
    }

    override fun logCollectibleReceiveEvent() {
        coroutineScope?.launch { collectibleEventTracker.logCollectibleReceiveEvent() }
    }

    override fun updateSearchKeyword(query: String) {
        searchKeywordFlow.value = query
    }

    override fun saveNFTListingViewTypePreference(nftListingViewType: NFTListingViewType) {
        nftListingTypeFlow.value = nftListingViewType
        coroutineScope?.launch { saveNFTListingViewTypePreferenceUseCase(nftListingViewType) }
    }

    private fun initSearchKeywordFlow(accountLites: Map<String, AccountLite>): Flow<ViewState> {
        val isThereAnyAuthAccount = accountLites.values.any { it.cachedInfo?.type?.canSignTransaction() == true }
        val addresses = accountLites.keys.toList()
        return combine(
            nftListingTypeFlow,
            getCollectibleItemsFlow(addresses),
            getCollectibleItemsCountFlow(addresses)
        ) { nftListingType, collectiblePagingData, count ->
            val collectibleList = collectiblePagingData.map { assetLite ->
                val accountLite = accountLites[assetLite.address]
                collectibleListItemMapper.createCollectibleListItem(assetLite, accountLite, nftListingType)
            }

            val contentStateType = when {
                count.totalCount == 0 -> Empty.NoCollectible
                count.totalFilteredOutCount == count.totalCount -> AllFilteredOut(count.totalCount)
                else -> Content(
                    collectibleList,
                    getCollectibleHeaderItems(count.filteredAndSearchQueriedCount, isThereAnyAuthAccount)
                )
            }
            ViewState.ContentState(isThereAnyAuthAccount, contentStateType)
        }
    }

    private fun getCollectibleHeaderItems(count: Int, isThereAnyAuth: Boolean): List<BaseCollectibleListHeaderItem> {
        val data = BaseCollectibleListHeaderItemProviderData(count, isThereAnyAuth, searchKeywordFlow.value)
        return headerItemProvider.getHeaders(data)
    }

    private fun getCollectibleItemsFlow(addresses: List<String>): Flow<PagingData<AssetLite>> {
        return getSearchKeywordFlow().flatMapLatest { searchKeyword ->
            collectibleListingPreviewUseCase.getCollectibleItemsFlow(searchKeyword, addresses)
        }.cachedIn(coroutineScope ?: return flowOf(PagingData.empty()))
    }

    private fun getCollectibleItemsCountFlow(addresses: List<String>): Flow<FilteredCollectibleCount> {
        return getSearchKeywordFlow().flatMapLatest { searchKeyword ->
            collectibleListingPreviewUseCase.getCollectibleLiteCountFlow(searchKeyword, addresses)
        }
    }

    private fun getSearchKeywordFlow() = searchKeywordFlow.debounce(QUERY_DEBOUNCE).distinctUntilChanged()

    companion object {
        private const val QUERY_DEBOUNCE = 300L
    }
}
