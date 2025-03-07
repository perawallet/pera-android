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

package com.algorand.android.modules.swap.previewsummary.ui

import android.content.res.Resources
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.swap.assetswap.domain.model.SwapQuote
import com.algorand.android.modules.swap.previewsummary.ui.model.SwapPreviewSummaryPreview
import com.algorand.android.modules.swap.previewsummary.ui.usecase.SwapPreviewSummaryPreviewUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SwapPreviewSummaryViewModel @Inject constructor(
    private val swapPreviewSummaryPreviewUseCase: SwapPreviewSummaryPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val swapQuote = savedStateHandle.getOrThrow<SwapQuote>(SWAP_QUOTE_KEY)

    private val _swapPreviewSummaryPreviewFlow = MutableStateFlow<SwapPreviewSummaryPreview?>(null)
    val swapPreviewSummaryPreviewFlow: StateFlow<SwapPreviewSummaryPreview?>
        get() = _swapPreviewSummaryPreviewFlow.asStateFlow()

    fun initializePreview() {
        viewModelScope.launchIO {
            _swapPreviewSummaryPreviewFlow.value = swapPreviewSummaryPreviewUseCase.getInitialPreview(swapQuote)
        }
    }

    fun getUpdatedPriceRatio(resources: Resources): AnnotatedString? {
        return _swapPreviewSummaryPreviewFlow.value?.getSwitchedPriceRatio(resources)
    }

    companion object {
        private const val SWAP_QUOTE_KEY = "swapQuote"
    }
}
