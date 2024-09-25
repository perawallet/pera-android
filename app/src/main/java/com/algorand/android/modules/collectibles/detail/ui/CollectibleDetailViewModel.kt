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

package com.algorand.android.modules.collectibles.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.asset.detail.ui.nftdetail.model.CollectibleDetailPreview
import com.algorand.android.module.asset.detail.ui.nftdetail.usecase.GetCollectibleDetailPreview
import com.algorand.android.foundation.Event
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.collectibles.detail.base.ui.BaseCollectibleDetailViewModel
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CollectibleDetailViewModel @Inject constructor(
    private val getCollectibleDetailPreview: GetCollectibleDetailPreview,
    getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug,
    savedStateHandle: SavedStateHandle
) : BaseCollectibleDetailViewModel(getActiveNodeNetworkSlug) {

    val nftId = savedStateHandle.getOrThrow<Long>(COLLECTIBLE_ASSET_ID_KEY)
    val accountAddress = savedStateHandle.getOrThrow<String>(PUBLIC_KEY_KEY)

    private val _nftDetailPreviewFlow = MutableStateFlow<CollectibleDetailPreview?>(null)
    val nftDetailPreviewFlow: StateFlow<CollectibleDetailPreview?> get() = _nftDetailPreviewFlow

    init {
        getCollectibleDetailPreview()
    }

    fun getAssetName(): AssetName? {
        return nftDetailPreviewFlow.value?.nftName
    }

    fun getExplorerUrl(): String? {
        return nftDetailPreviewFlow.value?.peraExplorerUrl
    }

    fun onSendNFTClick() {
        with(_nftDetailPreviewFlow) {
            update { it?.copy(collectibleSendEvent = Event(Unit)) }
        }
    }

    fun getOptOutAssetAction(): AssetAction {
        return AssetAction(
            publicKey = accountAddress,
            assetId = nftId,
            assetFullName = getAssetName()?.assetName
        )
    }

    private fun getCollectibleDetailPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            getCollectibleDetailPreview(
                nftId = nftId,
                accountAddress = accountAddress
            ).collectLatest {
                _nftDetailPreviewFlow.emit(it)
            }
        }
    }

    companion object {
        private const val COLLECTIBLE_ASSET_ID_KEY = "collectibleAssetId"
        private const val PUBLIC_KEY_KEY = "publicKey"
    }
}
