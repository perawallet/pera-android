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

package com.algorand.android.modules.swap.transactionstatus.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.foundation.Event
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusPreview
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType.COMPLETED
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType.FAILED
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType.SENDING
import com.algorand.android.module.swap.ui.txnstatus.usecase.GetSwapTransactionStatusPreviewFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SwapTransactionStatusViewModel @Inject constructor(
    private val getSwapTransactionStatusPreviewFlow: GetSwapTransactionStatusPreviewFlow,
    private val getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = SwapTransactionStatusFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val swapQuote: SwapQuote
        get() = args.swapTransactionStatusNavArgs.swapQuote

    private val _swapTransactionStatusPreviewFlow = MutableStateFlow<SwapTransactionStatusPreview?>(null)
    val swapTransactionStatusPreviewFlow: StateFlow<SwapTransactionStatusPreview?>
        get() = _swapTransactionStatusPreviewFlow

    fun getNetworkSlug(): String = getActiveNodeNetworkSlug()

    fun onPrimaryButtonClicked(swapTransactionStatusType: SwapTransactionStatusType) {
        viewModelScope.launch {
            when (swapTransactionStatusType) {
                FAILED -> onTryAgainClick()
                COMPLETED -> onSwapDoneClick()
                SENDING -> Unit
            }
        }
    }

    fun initSwapTransactionStatusPreviewFlow() {
        if (_swapTransactionStatusPreviewFlow.value == null) {
            viewModelScope.launch {
                getSwapTransactionStatusPreviewFlow(args.swapTransactionStatusNavArgs).collectLatest { preview ->
                    _swapTransactionStatusPreviewFlow.emit(preview)
                }
            }
        }
    }

    fun getOptInTransactionsFees(): Long = args.swapTransactionStatusNavArgs.optInTransactionsFees

    fun getAlgorandTransactionFees(): Long = args.swapTransactionStatusNavArgs.algorandTransactionsFees

    private fun onSwapDoneClick() {
        _swapTransactionStatusPreviewFlow.value?.let { preview ->
            _swapTransactionStatusPreviewFlow.value = preview.copy(navigateBackEvent = Event(Unit))
        }
    }

    private fun onTryAgainClick() {
        _swapTransactionStatusPreviewFlow.value?.let { preview ->
            _swapTransactionStatusPreviewFlow.value = preview.copy(navigateToAssetSwapFragmentEvent = Event(Unit))
        }
    }
}
