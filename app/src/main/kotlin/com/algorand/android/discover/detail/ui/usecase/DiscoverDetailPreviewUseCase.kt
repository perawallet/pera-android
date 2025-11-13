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

package com.algorand.android.discover.detail.ui.usecase

import android.content.SharedPreferences
import androidx.navigation.NavDirections
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.discover.common.domain.DiscoverActionRequest
import com.algorand.android.discover.common.ui.model.DiscoverAction
import com.algorand.android.discover.common.ui.model.OpenSystemBrowserRequest
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.discover.detail.ui.DiscoverDetailFragmentDirections
import com.algorand.android.discover.detail.ui.mapper.BuySellActionRequestMapper
import com.algorand.android.discover.detail.ui.model.BuySellActionRequest
import com.algorand.android.discover.detail.ui.model.DiscoverDetailPreview
import com.algorand.android.discover.home.domain.model.DappInfo
import com.algorand.android.discover.home.domain.model.TokenDetailInfo
import com.algorand.android.discover.home.ui.mapper.DiscoverDappFavoritesMapper
import com.algorand.android.discover.utils.getSendDeviceId
import com.algorand.android.discover.utils.isValidDiscoverURL
import com.algorand.android.modules.swap.utils.DiscoverSwapNavigationDestinationHelper
import com.algorand.android.modules.tracking.discover.detail.DiscoverDetailEventTracker
import com.algorand.android.utils.Event
import com.algorand.android.utils.fromJson
import com.algorand.android.utils.preference.getSavedThemePreference
import com.google.gson.Gson
import javax.inject.Inject

class DiscoverDetailPreviewUseCase @Inject constructor(
    private val buySellActionRequestMapper: BuySellActionRequestMapper,
    private val sharedPreferences: SharedPreferences,
    private val discoverSwapNavigationDestinationHelper: DiscoverSwapNavigationDestinationHelper,
    private val discoverDetailEventTracker: DiscoverDetailEventTracker,
    private val deviceIdUseCase: DeviceIdUseCase,
    private val discoverDappFavoritesMapper: DiscoverDappFavoritesMapper,
    private val gson: Gson
) {

    fun getInitialStatePreview(tokenDetail: TokenDetailInfo) = DiscoverDetailPreview(
        themePreference = sharedPreferences.getSavedThemePreference(),
        isLoading = true,
        reloadPageEvent = Event(Unit),
        tokenDetail = tokenDetail
    )

    fun onPageRequestedShouldOverrideUrlLoading(
        previousState: DiscoverDetailPreview,
        url: String
    ) = previousState.copy(
        externalPageRequestedEvent = Event(url)
    )

    fun onPageFinished(previousState: DiscoverDetailPreview) = previousState.copy(
        isLoading = false
    )

    fun onError(previousState: DiscoverDetailPreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.NO_CONNECTION)
    )

    fun onHttpError(previousState: DiscoverDetailPreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.HTTP_ERROR)
    )

    fun getOpenSystemBrowserRequestFromJson(json: String): OpenSystemBrowserRequest? {
        return gson.fromJson<OpenSystemBrowserRequest>(json)
    }

    suspend fun logTokenDetailActionButtonClick(jsonEncodedPayload: String) {
        val detailActionRequest = getDetailActionRequestFromJson(jsonEncodedPayload)

        detailActionRequest?.let {
            logDetailAction(
                discoverActionRequest = it,
                assetIn = it.assetIn?.toLongOrNull() ?: -1,
                assetOut = it.assetOut?.toLongOrNull() ?: -1
            )
        }
    }

    suspend fun handleTokenDetailActionButtonClick(
        data: String,
        previousState: DiscoverDetailPreview
    ): DiscoverDetailPreview {
        val detailActionRequest = getDetailActionRequestFromJson(data)

        val buySellActionRequest = buySellActionRequestMapper.mapToBuySellActionRequest(
            assetInId = detailActionRequest?.assetIn?.toLongOrNull() ?: -1,
            assetOutId = detailActionRequest?.assetOut?.toLongOrNull() ?: -1,
            detailAction = detailActionRequest?.action
        )
        var swapNavDirection: NavDirections? = null
        when (buySellActionRequest.destination) {
            BuySellActionRequest.Destination.MELD -> {
                swapNavDirection = DiscoverDetailFragmentDirections.actionDiscoverDetailFragmentToMeldNavigation()
            }

            BuySellActionRequest.Destination.SWAP -> {
                discoverSwapNavigationDestinationHelper.getSwapNavigationDestination(
                    onNavToIntroduction = {
                        swapNavDirection = DiscoverDetailFragmentDirections
                            .actionDiscoverDetailFragmentToSwapIntroductionNavigation(
                                fromAssetId = buySellActionRequest.assetInId ?: -1L,
                                toAssetId = buySellActionRequest.assetOutId ?: -1L
                            )
                    },
                    onNavToAccountSelection = {
                        swapNavDirection = DiscoverDetailFragmentDirections
                            .actionDiscoverDetailFragmentToSwapAccountSelectionNavigation(
                                fromAssetId = buySellActionRequest.assetInId ?: -1L,
                                toAssetId = buySellActionRequest.assetOutId ?: -1L
                            )
                    },
                    onNavToSwapV2 = {
                        swapNavDirection = DiscoverDetailFragmentDirections
                            .actionDiscoverDetailFragmentToSwapV2Navigation(
                                assetInId = buySellActionRequest.assetInId ?: -1L,
                                assetOutId = buySellActionRequest.assetOutId ?: -1L
                            )
                    }
                )
            }

            BuySellActionRequest.Destination.ONRAMP -> {}
            else -> {}
        }
        return swapNavDirection?.let { direction ->
            previousState.copy(buySellActionEvent = Event(direction))
        } ?: previousState
    }

    fun getSendDeviceIdJSFunctionOrNull(callingUrl: String): String? {
        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId()
        return if (deviceId != null && isValidDiscoverURL(callingUrl)) {
            getSendDeviceId(deviceId, gson)
        } else {
            null
        }
    }

    private fun getDetailActionRequestFromJson(jsonEncodedPayload: String): DiscoverActionRequest? {
        return gson.fromJson<DiscoverActionRequest>(jsonEncodedPayload)
    }

    private suspend fun logDetailAction(
        discoverActionRequest: DiscoverActionRequest,
        assetIn: Long,
        assetOut: Long
    ) {
        when (discoverActionRequest.action) {
            DiscoverAction.BUY_ALGO, DiscoverAction.SWAP_TO_TOKEN -> {
                discoverDetailEventTracker.logTokenDetailBuyEvent(
                    assetIn = assetIn,
                    assetOut = assetOut
                )
            }

            DiscoverAction.SWAP_FROM_ALGO, DiscoverAction.SWAP_FROM_TOKEN -> {
                discoverDetailEventTracker.logTokenDetailSellEvent(
                    assetIn = assetIn,
                    assetOut = assetOut
                )
            }

            DiscoverAction.UNKNOWN, null -> {
                // No log action defined here
            }
        }
    }

    fun pushDappViewerScreen(
        data: String,
        previousState: DiscoverDetailPreview
    ): DiscoverDetailPreview {
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
}
