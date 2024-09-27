/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.dapp.bidali.ui.browser.usecase

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsData
import com.algorand.android.module.account.core.component.caching.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.module.foundation.Event
import com.algorand.android.modules.dapp.bidali.data.mapper.BidaliOpenUrlRequestMapper
import com.algorand.android.modules.dapp.bidali.data.mapper.BidaliPaymentRequestMapper
import com.algorand.android.modules.dapp.bidali.data.model.BidaliOpenUrlRequest
import com.algorand.android.modules.dapp.bidali.data.model.BidaliPaymentRequest
import com.algorand.android.modules.dapp.bidali.domain.mapper.BidaliAssetMapper
import com.algorand.android.modules.dapp.bidali.getCompiledUpdatedBalancesJavascript
import com.algorand.android.modules.dapp.bidali.ui.browser.model.BidaliBrowserPreview
import com.algorand.android.modules.peraserializer.PeraSerializer
import com.algorand.android.module.node.domain.usecase.IsSelectedNodeMainnet
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.getBaseUrlOrNull
import javax.inject.Inject
import kotlinx.coroutines.flow.channelFlow

class BidaliBrowserPreviewUseCase @Inject constructor(
    private val bidaliAssetMapper: BidaliAssetMapper,
    private val isSelectedNodeMainnet: IsSelectedNodeMainnet,
    private val peraSerializer: PeraSerializer,
    private val bidaliPaymentRequestMapper: BidaliPaymentRequestMapper,
    private val bidaliOpenUrlRequestMapper: BidaliOpenUrlRequestMapper,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets,
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData,
    private val getBidaliPaymentSendTransactionPayload: GetBidaliPaymentSendTransactionPayload
) {

    fun getInitialStatePreview(
        title: String,
        url: String
    ) = BidaliBrowserPreview(
        isLoading = true,
        reloadPageEvent = Event(Unit),
        title = title,
        url = url,
        toolbarSubtitle = getBaseUrlOrNull(url) ?: emptyString()
    )

    fun requestLoadHomepage(
        previousState: BidaliBrowserPreview,
        title: String,
        url: String
    ) = previousState.copy(
        isLoading = true,
        reloadPageEvent = Event(Unit),
        title = title,
        url = url
    )

    fun onPreviousNavButtonClicked(previousState: BidaliBrowserPreview) = previousState.copy(
        webViewGoBackEvent = Event(Unit)
    )

    fun onNextNavButtonClicked(previousState: BidaliBrowserPreview) = previousState.copy(
        webViewGoForwardEvent = Event(Unit)
    )

    fun onPageStarted(previousState: BidaliBrowserPreview) = previousState.copy(
        isLoading = true,
        pageStartedEvent = Event(Unit)
    )

    fun onPageFinished(
        previousState: BidaliBrowserPreview,
        title: String?,
        url: String?
    ) = previousState.copy(
        isLoading = false,
        title = title ?: previousState.title,
        url = url ?: previousState.url,
        toolbarSubtitle = getBaseUrlOrNull(url) ?: previousState.toolbarSubtitle
    )

    fun onError(previousState: BidaliBrowserPreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.NO_CONNECTION)
    )

    fun onPageUrlChanged(previousState: BidaliBrowserPreview) = previousState.copy(
        pageUrlChangedEvent = Event(Unit)
    )

    fun onHttpError(previousState: BidaliBrowserPreview) = previousState.copy(
        isLoading = false,
        loadingErrorEvent = Event(WebViewError.HTTP_ERROR)
    )

    suspend fun onPaymentRequest(
        data: String,
        previousState: BidaliBrowserPreview,
        accountAddress: String
    ): BidaliBrowserPreview {
        return try {
            val payload = peraSerializer.fromJson(data, BidaliPaymentRequest::class.java) ?: return previousState
            val request = bidaliPaymentRequestMapper.mapToBidaliPaymentRequestDTO(payload) ?: return previousState
            val transaction = getBidaliPaymentSendTransactionPayload(request, accountAddress) ?: return previousState
            previousState.copy(onPaymentRequestEvent = Event(transaction))
        } catch (exception: Exception) {
            previousState
        }
    }

    fun openUrl(
        data: String,
        previousState: BidaliBrowserPreview
    ): BidaliBrowserPreview {
        return try {
            val payload = peraSerializer.fromJson(data, BidaliOpenUrlRequest::class.java) ?: return previousState
            val openUrlRequest = bidaliOpenUrlRequestMapper.mapToBidaliOpenUrlRequestDTO(payload)
            openUrlRequest?.let {
                previousState.copy(openUrlRequestEvent = Event(it))
            } ?: previousState
        } catch (exception: Exception) {
            previousState
        }
    }

    fun generateUpdatedBalancesJavascript(
        previousState: BidaliBrowserPreview,
        accountAddress: String
    ) = channelFlow {
        fetchAccountInformationAndCacheAssets(accountAddress)
        send(
            previousState.copy(
                updatedBalancesJavascript = getCompiledUpdatedBalancesJavascript(
                    bidaliAssetMapper.mapFromOwnedAssetData(
                        ownedAssetDataList = getAccountOwnedAssetsData(accountAddress, true),
                        isMainnet = isSelectedNodeMainnet()
                    )
                )
            )
        )
    }
}
