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

package com.algorand.android.modules.assetinbox.send.warning.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.algorand.android.modules.assetinbox.send.warning.ui.model.Arc59SendSummaryWarningNavArgs
import com.algorand.android.modules.assetinbox.send.warning.ui.usecase.Arc59SendSummaryWarningPreviewUseCase
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class Arc59SendSummaryWarningViewModel @Inject constructor(
    private val arc59SendSummaryWarningPreviewUseCase: Arc59SendSummaryWarningPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.getOrThrow<Arc59SendSummaryWarningNavArgs>(
        ARC_59_SEND_SUMMARY_WARNING_NAV_ARGS_KEY
    )

    fun getWarningTitle(): String {
        return arc59SendSummaryWarningPreviewUseCase.getArc59SendSummaryWarningPreview(args).title
    }

    fun getWarningDescription(): String {
        return arc59SendSummaryWarningPreviewUseCase.getArc59SendSummaryWarningPreview(args).detail
    }

    companion object {
        const val ARC_59_SEND_SUMMARY_WARNING_NAV_ARGS_KEY = "arc59SendSummaryWarningNavArgs"
    }
}
