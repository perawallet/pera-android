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

package com.algorand.android.discover.home.ui.usecase

import android.content.SharedPreferences
import androidx.navigation.NavDirections
import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.assetsearch.domain.mapper.AssetSearchQueryMapper
import com.algorand.android.assetsearch.domain.pagination.AssetSearchPagerBuilder
import com.algorand.android.discover.common.domain.DiscoverActionRequest
import com.algorand.android.discover.common.ui.model.DiscoverAction
import com.algorand.android.discover.common.ui.model.OpenSystemBrowserRequest
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.discover.detail.ui.mapper.BuySellActionRequestMapper
import com.algorand.android.discover.detail.ui.model.BuySellActionRequest
import com.algorand.android.discover.home.domain.model.DappInfo
import com.algorand.android.discover.home.domain.model.TokenDetailInfo
import com.algorand.android.discover.home.domain.model.UrlElement
import com.algorand.android.discover.home.domain.usecase.DiscoverSearchAssetUseCase
import com.algorand.android.discover.home.ui.DiscoverHomeFragmentDirections
import com.algorand.android.discover.home.ui.mapper.DiscoverAssetItemMapper
import com.algorand.android.discover.home.ui.mapper.DiscoverDappFavoritesMapper
import com.algorand.android.discover.home.ui.model.DiscoverAssetItem
import com.algorand.android.discover.home.ui.model.DiscoverHomePreview
import com.algorand.android.modules.swap.assetswap.data.utils.getSafeAssetIdForResponse
import com.algorand.android.modules.tracking.discover.home.DiscoverHomeEventTracker
import com.algorand.android.utils.Event
import com.algorand.android.utils.fromJson
import com.algorand.android.utils.preference.getSavedThemePreference
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiscoverHomePreviewUseCase @Inject constructor(
    private val discoverSearchAssetUseCase: DiscoverSearchAssetUseCase,
    private val assetSearchQueryMapper: AssetSearchQueryMapper,
    private val discoverAssetItemMapper: DiscoverAssetItemMapper,
    private val sharedPreferences: SharedPreferences,
    private val discoverDappFavoritesMapper: DiscoverDappFavoritesMapper,
    private val gson: Gson,
    private val discoverHomeEventTracker: DiscoverHomeEventTracker,
    private val buySellActionRequestMapper: BuySellActionRequestMapper,
) {

    fun getSearchPaginationFlow(
        searchPagerBuilder: AssetSearchPagerBuilder,
        scope: CoroutineScope,
        queryText: String
    ): Flow<PagingData<DiscoverAssetItem>> {
        val assetSearchQuery = assetSearchQueryMapper.mapToAssetSearchQuery(
            queryText = queryText,
            hasCollectibles = null,
            availableOnDiscoverMobile = true,
            defaultToTrending = true
        )
        val searchedAssetsFlow = discoverSearchAssetUseCase.createPaginationFlow(
            builder = searchPagerBuilder,
            scope = scope,
            defaultQuery = assetSearchQuery
        )

        return searchedAssetsFlow.map { baseSearchedAssetPagination ->
            baseSearchedAssetPagination.map { discoverSearchedAsset ->
                discoverAssetItemMapper.mapToDiscoverAssetItem(discoverSearchedAsset)
            }
        }
    }

    suspend fun searchAsset(queryText: String) {
        val assetSearchQuery = assetSearchQueryMapper.mapToAssetSearchQuery(
            queryText = queryText,
            hasCollectibles = null,
            availableOnDiscoverMobile = true,
            defaultToTrending = true
        )
        discoverSearchAssetUseCase.searchAsset(assetSearchQuery)
    }

    fun getInitialStatePreview(url: String?) = DiscoverHomePreview(
        themePreference = sharedPreferences.getSavedThemePreference(),
        isLoading = true,
        tokenDetailScreenRequestEvent = null,
        dappViewerScreenRequestEvent = null,
        urlElementRequestEvent = null,
        loadHomeEvent = if (url.isNullOrBlank()) Event(Unit) else null,
        loadCustomUrlEvent = if (!url.isNullOrBlank()) Event(url) else null
    )

    fun getPreviewWithHandleQueryChangeForScrollEvent(previousPreview: DiscoverHomePreview) = previousPreview.copy(
        handleQueryChangeForScrollEvent = Event(Unit)
    )

    fun updateSearchScreenLoadState(
        isListEmpty: Boolean,
        isCurrentStateError: Boolean,
        isLoading: Boolean,
        previousState: DiscoverHomePreview
    ): DiscoverHomePreview {
        val scrollToTopEvent =
            if (isLoading.not()) previousState.handleQueryChangeForScrollEvent?.consume()?.run { Event(Unit) } else null
        return previousState.copy(
            isListEmpty = isListEmpty &&
                    !isCurrentStateError &&
                    !isLoading &&
                    previousState.isSearchActivated,
            scrollToTopEvent = scrollToTopEvent
        )
    }

    fun requestSearchVisible(
        isVisible: Boolean,
        previousState: DiscoverHomePreview
    ) = previousState.copy(
        isListEmpty = if (isVisible) previousState.isListEmpty else false,
        isSearchActivated = isVisible
    )

    fun requestLoadHomepage(previousState: DiscoverHomePreview) = previousState.copy(
        isLoading = true,
        loadHomeEvent = Event(Unit)
    )

    fun onPageRequestedShouldOverrideUrlLoading(previousState: DiscoverHomePreview) = previousState.copy(
        isLoading = true
    )

    fun onPageFinished(previousState: DiscoverHomePreview) = previousState.copy(
        isLoading = false
    )

    fun onError(previousState: DiscoverHomePreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.NO_CONNECTION)
    )

    fun onHttpError(previousState: DiscoverHomePreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.HTTP_ERROR)
    )

    fun pushDappViewerScreen(
        data: String,
        previousState: DiscoverHomePreview
    ): DiscoverHomePreview {
        val dappInfo = gson.fromJson(data, DappInfo::class.java)
        return previousState.copy(
            dappViewerScreenRequestEvent = Event(
                Pair(
                    dappInfo,
                    dappInfo.favorites?.map {
                        discoverDappFavoritesMapper.mapToDappFavoriteElement(it)
                    }?.toTypedArray() ?: emptyArray()
                )
            )
        )
    }

    fun pushNewScreen(
        data: String,
        previousState: DiscoverHomePreview
    ) = previousState.copy(
        urlElementRequestEvent = Event(
            gson.fromJson(data, UrlElement::class.java)
        )
    )

    fun pushTokenDetailScreen(
        data: String,
        previousState: DiscoverHomePreview
    ) = previousState.copy(
        tokenDetailScreenRequestEvent = Event(
            gson.fromJson(data, TokenDetailInfo::class.java)
        )
    )

    fun getOpenSystemBrowserRequestFromJson(json: String): OpenSystemBrowserRequest? {
        return gson.fromJson<OpenSystemBrowserRequest>(json)
    }

    suspend fun handleTokenDetailActionButtonClick(
        data: String,
        previousState: DiscoverHomePreview
    ): DiscoverHomePreview {
        val detailActionRequest = getDetailActionRequestFromJson(data)

        val buySellActionRequest = buySellActionRequestMapper.mapToBuySellActionRequest(
            assetInId = getSafeAssetIdForResponse(detailActionRequest?.assetIn?.toLongOrNull()) ?: -1,
            assetOutId = getSafeAssetIdForResponse(detailActionRequest?.assetOut?.toLongOrNull()) ?: -1,
            detailAction = detailActionRequest?.action
        )
        var swapNavDirection: NavDirections? = null
        when (buySellActionRequest.destination) {
            BuySellActionRequest.Destination.MELD -> {
                swapNavDirection = DiscoverHomeFragmentDirections.actionDiscoverHomeFragmentToMeldNavigation()
            }

            BuySellActionRequest.Destination.SWAP -> {
                swapNavDirection = DiscoverHomeFragmentDirections.actionDiscoverHomeFragmentToSwapV2Navigation(
                    assetInId = buySellActionRequest.assetInId ?: -1L,
                    assetOutId = buySellActionRequest.assetOutId ?: -1L
                )
            }

            else -> {}
        }
        return swapNavDirection?.let { direction ->
            previousState.copy(buySellActionEvent = Event(direction))
        } ?: previousState
    }

    private fun getDetailActionRequestFromJson(jsonEncodedPayload: String): DiscoverActionRequest? {
        return gson.fromJson<DiscoverActionRequest>(jsonEncodedPayload)
    }

    suspend fun logTokenDetailActionButtonClick(jsonEncodedPayload: String) {
        val detailActionRequest = getDetailActionRequestFromJson(jsonEncodedPayload)

        detailActionRequest?.let {
            logDetailAction(
                discoverActionRequest = it,
                assetIn = getSafeAssetIdForResponse(it.assetIn?.toLongOrNull()) ?: -1,
                assetOut = getSafeAssetIdForResponse(it.assetOut?.toLongOrNull()) ?: -1
            )
        }
    }

    private suspend fun logDetailAction(
        discoverActionRequest: DiscoverActionRequest,
        assetIn: Long,
        assetOut: Long
    ) {
        when (discoverActionRequest.action) {
            DiscoverAction.BUY_ALGO, DiscoverAction.SWAP_TO_TOKEN -> {
                discoverHomeEventTracker.logTokenDetailBuyEvent(
                    assetIn = assetIn,
                    assetOut = assetOut
                )
            }

            DiscoverAction.SWAP_FROM_ALGO, DiscoverAction.SWAP_FROM_TOKEN -> {
                discoverHomeEventTracker.logTokenDetailSellEvent(
                    assetIn = assetIn,
                    assetOut = assetOut
                )
            }

            DiscoverAction.UNKNOWN, null -> {
                // No log action defined here
            }
        }
    }
}
