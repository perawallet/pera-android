/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryNavArgs
import com.algorand.android.modules.assetinbox.send.ui.model.Arc59SendSummaryPreview
import com.algorand.android.modules.assetinbox.send.ui.usecase.Arc59SendSummaryPreviewUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class Arc59SendSummaryViewModel @Inject constructor(
    private val arc59SendSummaryPreviewUseCase: Arc59SendSummaryPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(arc59SendSummaryPreviewUseCase.getInitialPreview())
    val viewStateFlow: StateFlow<Arc59SendSummaryPreview> = _viewStateFlow.asStateFlow()

    private val args = savedStateHandle.getOrThrow<Arc59SendSummaryNavArgs>(ARC_59_SEND_SUMMARY_NAV_ARGS_KEY)

    fun initializePreview() {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.getArc59SendSummaryPreview(
                _viewStateFlow.value,
                args.receiverPublicKey,
                args.assetId,
                args.assetAmount
            ).collectLatest { preview ->
                _viewStateFlow.value = preview
            }
        }
    }

    fun createTransactionData() {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.createArc59SendTransactionData(args, _viewStateFlow.value).collectLatest {
                _viewStateFlow.value = it
            }
        }
    }

    fun sendSignedTransaction(signedTransactions: List<Any?>) {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.sendSignedTransaction(
                _viewStateFlow.value,
                signedTransactions
            ).collectLatest {
                _viewStateFlow.value = it
            }
        }
    }

    companion object {
        const val ARC_59_SEND_SUMMARY_NAV_ARGS_KEY = "arc59SendSummaryNavArgs"
    }
}
