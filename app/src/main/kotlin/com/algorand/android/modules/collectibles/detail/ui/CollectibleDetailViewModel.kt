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
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import com.algorand.android.modules.collectibles.detail.base.ui.BaseCollectibleDetailViewModel
import com.algorand.android.modules.collectibles.detail.base.ui.model.BaseCollectibleMediaItem
import com.algorand.android.modules.collectibles.detail.ui.CollectibleDetailViewModel.ViewState
import com.algorand.android.modules.collectibles.detail.ui.model.NFTDetailPreview
import com.algorand.android.modules.collectibles.detail.ui.usecase.CollectibleDetailPreviewUseCase
import com.algorand.android.modules.collectibles.download.DownloadFileUseCase
import com.algorand.android.usecase.NetworkSlugUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class CollectibleDetailViewModel @Inject constructor(
    private val collectibleDetailPreviewUseCase: CollectibleDetailPreviewUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    networkSlugUseCase: NetworkSlugUseCase,
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>
) : BaseCollectibleDetailViewModel(networkSlugUseCase), StateViewModel<ViewState> by stateDelegate {

    val nftId = savedStateHandle.getOrThrow<Long>(COLLECTIBLE_ASSET_ID_KEY)
    val accountAddress = savedStateHandle.getOrThrow<String>(PUBLIC_KEY_KEY)

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        getCollectibleDetailPreview()
    }

    fun getAssetName(): AssetName? {
        return getPreview()?.nftName
    }

    fun getMediaByIndex(index: Int): BaseCollectibleMediaItem? {
        return getPreview()?.mediaListOfNFT?.get(index)
    }

    fun getExplorerUrl(): String? {
        return getPreview()?.peraExplorerUrl
    }

    fun onSendNFTClick() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val updatedPreview = collectibleDetailPreviewUseCase.getSendEventPreviewAccordingToNFTType(
                contentState.preview
            )
            updateContentState(updatedPreview)
        }
    }

    fun onSaveNFTClick(mediaIndex: Int) {
        getMediaByIndex(mediaIndex)?.let { mediaItem ->
            mediaItem.downloadUrl?.let { downloadUrl ->
                val collectibleId = mediaItem.collectibleId.toString()
                val mediaExtension = mediaItem.mediaExtension.orEmpty()
                val fileName = "$collectibleId$mediaExtension"
                downloadFileUseCase.execute(downloadUrl, fileName)
            }
        }
    }

    private fun getPreview(): NFTDetailPreview? {
        return (stateDelegate.state.value as? ViewState.Content)?.preview
    }

    private fun getCollectibleDetailPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedPreview = collectibleDetailPreviewUseCase.getCollectibleDetailPreview(
                nftId = nftId,
                accountAddress = accountAddress
            )
            updateContentState(updatedPreview)
        }
    }

    private fun updateContentState(preview: NFTDetailPreview?) {
        if (preview != null) {
            stateDelegate.updateState {
                ViewState.Content(preview)
            }
        }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Content(val preview: NFTDetailPreview) : ViewState
    }

    companion object {
        private const val COLLECTIBLE_ASSET_ID_KEY = "collectibleAssetId"
        private const val PUBLIC_KEY_KEY = "publicKey"
    }
}
