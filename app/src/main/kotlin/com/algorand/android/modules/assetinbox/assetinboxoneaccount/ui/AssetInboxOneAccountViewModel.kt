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

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper.Arc59ReceiveDetailNavArgsMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AsaPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountNavArgs
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountPreview
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.usecase.AssetInboxOneAccountPreviewUseCase
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class AssetInboxOneAccountViewModel @Inject constructor(
    private val assetInboxOneAccountPreviewUseCase: AssetInboxOneAccountPreviewUseCase,
    private val arc59ReceiveDetailNavArgsMapper: Arc59ReceiveDetailNavArgsMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(assetInboxOneAccountPreviewUseCase.getInitialPreview())
    val viewStateFlow: StateFlow<AssetInboxOneAccountPreview> = _viewStateFlow.asStateFlow()

    private val args =
        savedStateHandle.getOrThrow<AssetInboxOneAccountNavArgs>(ASSET_INBOX_ONE_ACCOUNT_NAV_ARGS_KEY)

    fun initializePreview() {
        viewModelScope.launchIO {
            assetInboxOneAccountPreviewUseCase.getAssetInboxOneAccountPreview(
                _viewStateFlow.value,
                args.address
            ).collectLatest { preview ->
                _viewStateFlow.value = preview
            }
        }
    }

    fun getArc59ReceiveDetailNavArgs(asaPreview: AsaPreview): Arc59ReceiveDetailNavArgs {
        return arc59ReceiveDetailNavArgsMapper.invoke(asaPreview)
    }

    companion object {
        const val ASSET_INBOX_ONE_ACCOUNT_NAV_ARGS_KEY = "assetInboxOneAccountNavArgs"
    }
}
