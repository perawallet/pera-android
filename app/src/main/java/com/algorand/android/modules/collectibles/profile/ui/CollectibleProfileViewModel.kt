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

package com.algorand.android.modules.collectibles.profile.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleProfilePreview
import com.algorand.android.assetdetailui.detail.nftprofile.usecase.GetCollectibleProfilePreviewFlow
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.collectibles.detail.base.ui.BaseCollectibleDetailViewModel
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CollectibleProfileViewModel @Inject constructor(
    private val getCollectibleProfilePreviewFlow: GetCollectibleProfilePreviewFlow,
    getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug,
    savedStateHandle: SavedStateHandle
) : BaseCollectibleDetailViewModel(getActiveNodeNetworkSlug) {

    val accountAddress = savedStateHandle.getOrThrow<String>(ACCOUNT_ADDRESS_KEY)
    val collectibleId = savedStateHandle.getOrThrow<Long>(COLLECTIBLE_ID_KEY)

    private val _collectibleProfilePreviewFlow = MutableStateFlow<CollectibleProfilePreview?>(null)
    val collectibleProfilePreviewFlow: StateFlow<CollectibleProfilePreview?> get() = _collectibleProfilePreviewFlow

    init {
        initCollectibleProfilePreviewFlow()
    }

    fun getAssetAction(): AssetAction {
        return AssetAction(
            assetId = collectibleId,
            publicKey = accountAddress,
            assetFullName = collectibleProfilePreviewFlow.value?.nftName?.assetName,
            assetShortName = collectibleProfilePreviewFlow.value?.nftShortName?.assetName
        )
    }

    fun getNFTExplorerUrl(): String? {
        return collectibleProfilePreviewFlow.value?.peraExplorerUrl
    }

    fun getNFTName(): AssetName? {
        return collectibleProfilePreviewFlow.value?.nftName
    }

    private fun initCollectibleProfilePreviewFlow() {
        viewModelScope.launch {
            getCollectibleProfilePreviewFlow(
                nftId = collectibleId,
                accountAddress = accountAddress
            ).collect { preview -> _collectibleProfilePreviewFlow.emit(preview) }
        }
    }

    companion object {
        private const val COLLECTIBLE_ID_KEY = "collectibleId"
        private const val ACCOUNT_ADDRESS_KEY = "accountAddress"
    }
}
