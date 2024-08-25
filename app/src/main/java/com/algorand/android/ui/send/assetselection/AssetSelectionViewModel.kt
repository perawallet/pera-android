/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.send.assetselection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.AssetTransaction
import com.algorand.android.nft.ui.model.AssetSelectionPreview
import com.algorand.android.usecase.AssetSelectionUseCase
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetSelectionViewModel @Inject constructor(
    private val assetSelectionUseCase: AssetSelectionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val assetTransaction = savedStateHandle.getOrThrow<AssetTransaction>(ASSET_TRANSACTION_KEY)

    val assetSelectionPreview: StateFlow<AssetSelectionPreview>
        get() = _assetSelectionPreview
    private val _assetSelectionPreview = MutableStateFlow(
        assetSelectionUseCase.getInitialStateOfAssetSelectionPreview(assetTransaction)
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            assetSelectionUseCase.getAssetSelectionListFlow(assetTransaction.senderAddress)
                .collectLatest { list ->
                    _assetSelectionPreview.emit(
                        _assetSelectionPreview.value.copy(
                            assetList = list,
                            isAssetListLoadingVisible = false
                        )
                    )
                }
        }
    }

    fun shouldShowTransactionTips(): Boolean {
        return assetSelectionUseCase.shouldShowTransactionTips()
    }

    fun updatePreviewWithSelectedAsset(assetId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            assetSelectionUseCase.getUpdatedPreviewFlowWithSelectedAsset(
                assetId = assetId,
                previousState = _assetSelectionPreview.value
            ).collectLatest { assetSelectionPreview ->
                _assetSelectionPreview.emit(assetSelectionPreview)
            }
        }
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
    }
}
