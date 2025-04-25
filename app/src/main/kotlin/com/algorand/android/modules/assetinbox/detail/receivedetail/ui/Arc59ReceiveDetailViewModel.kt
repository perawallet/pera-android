/*
 *   ~ Copyright 2022-2025 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.domain.Arc59ReceiveDetailPreviewUseCase
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailPreview
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class Arc59ReceiveDetailViewModel @Inject constructor(
    private val arc59ReceiveDetailPreviewUseCase: Arc59ReceiveDetailPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args =
        savedStateHandle.getOrThrow<Arc59ReceiveDetailNavArgs>(ARC_59_RECEIVE_DETAIL_NAV_ARGS)

    private val _previewFlow = MutableStateFlow<Arc59ReceiveDetailPreview?>(null)
    val previewFlow: StateFlow<Arc59ReceiveDetailPreview?> = _previewFlow.asStateFlow()

    fun initializePreview() {
        viewModelScope.launchIO {
            _previewFlow.value = arc59ReceiveDetailPreviewUseCase.getInitialPreview(args)
        }
    }

    fun rejectTransaction() {
        viewModelScope.launchIO {
            _previewFlow.value?.let { currentPreview ->
                arc59ReceiveDetailPreviewUseCase.createRejectTransaction(args, currentPreview).collectLatest {
                    _previewFlow.value = it
                }
            }
        }
    }

    fun claimTransaction() {
        viewModelScope.launchIO {
            _previewFlow.value?.let { currentPreview ->
                arc59ReceiveDetailPreviewUseCase.createClaimTransaction(args, currentPreview).collectLatest {
                    _previewFlow.value = it
                }
            }
        }
    }

    fun sendSignedTransaction(signedTransactions: List<Any?>) {
        viewModelScope.launchIO {
            _previewFlow.value?.let { currentPreview ->
                arc59ReceiveDetailPreviewUseCase
                    .sendSignedTransaction(signedTransactions, currentPreview)
                    .collectLatest { _previewFlow.value = it }
            }
        }
    }

    fun getGainOnRejectAmount() = args.gainOnReject.formatAsAlgoString()

    companion object {
        const val ARC_59_RECEIVE_DETAIL_NAV_ARGS = "arc59ReceiveDetailNavArgs"
    }
}
