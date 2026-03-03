/*
 *  Copyright 2022-2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.summary.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.assetinbox.send.summary.ui.model.Arc59SendSummaryNavArgs
import com.algorand.android.modules.assetinbox.send.summary.ui.model.Arc59SendSummaryPreview
import com.algorand.android.modules.assetinbox.send.summary.ui.usecase.Arc59SendSummaryPreviewUseCase
import com.algorand.android.modules.assetinbox.send.warning.ui.model.Arc59SendSummaryWarningNavArgs
import com.algorand.android.utils.browser.ASSET_INBOX_SUPPORT_URL
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class Arc59SendSummaryViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<Arc59SendSummaryPreview>,
    private val arc59SendSummaryPreviewUseCase: Arc59SendSummaryPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<Arc59SendSummaryPreview> by stateDelegate {

    private val args = savedStateHandle.getOrThrow<Arc59SendSummaryNavArgs>(ARC_59_SEND_SUMMARY_NAV_ARGS_KEY)

    init {
        stateDelegate.setDefaultState(arc59SendSummaryPreviewUseCase.getInitialPreview())
    }

    fun initializePreview() {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.getArc59SendSummaryPreview(
                state.value,
                args.receiverPublicKey,
                args.assetId,
                args.assetAmount
            ).collectLatest { preview ->
                stateDelegate.updateState { preview }
            }
        }
    }

    fun createTransactionData() {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.createArc59SendTransactionData(args, state.value).collectLatest { preview ->
                stateDelegate.updateState { preview }
            }
        }
    }

    fun sendSignedTransaction(signedTransactions: List<Any?>) {
        viewModelScope.launchIO {
            arc59SendSummaryPreviewUseCase.sendSignedTransaction(
                state.value,
                signedTransactions
            ).collectLatest { preview ->
                stateDelegate.updateState { preview }
            }
        }
    }

    fun getWarningMessage(): Arc59SendSummaryWarningNavArgs? {
        state.value.summary?.warningMessage?.let {
            return Arc59SendSummaryWarningNavArgs(it.title.orEmpty(), it.detail.orEmpty())
        }
        return null
    }

    fun getReadMoreUrl(): String {
        state.value.summary?.warningMessage?.let {
            return it.readMoreUrl ?: ASSET_INBOX_SUPPORT_URL
        }
        return ASSET_INBOX_SUPPORT_URL
    }

    companion object {
        const val ARC_59_SEND_SUMMARY_NAV_ARGS_KEY: String = "arc59SendSummaryNavArgs"
    }
}
