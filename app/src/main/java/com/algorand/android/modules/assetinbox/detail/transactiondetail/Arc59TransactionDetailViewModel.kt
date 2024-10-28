/*
 *   ~ Copyright 2022 Pera Wallet, LDA
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

package com.algorand.android.modules.assetinbox.detail.transactiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.algorand.android.modules.assetinbox.detail.transactiondetail.mapper.Arc59TransactionDetailPreviewMapper
import com.algorand.android.modules.assetinbox.detail.transactiondetail.model.Arc59TransactionDetailArgs
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class Arc59TransactionDetailViewModel @Inject constructor(
    previewMapper: Arc59TransactionDetailPreviewMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args =
        savedStateHandle.getOrThrow<Arc59TransactionDetailArgs>(ARC59_TRANSACTION_DETAIL_NAV_ARGS)

    private val _previewFlow = MutableStateFlow(previewMapper(args))
    val previewFlow = _previewFlow.asStateFlow()

    companion object {
        const val ARC59_TRANSACTION_DETAIL_NAV_ARGS = "arc59TransactionDetailNavArgs"
    }
}
