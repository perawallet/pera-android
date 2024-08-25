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

package com.algorand.android.ui.send.transferamount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.AssetTransferAmountPreview
import com.algorand.android.usecase.AssetTransferAmountPreviewUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.getOrElse
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AssetTransferAmountViewModel @Inject constructor(
    private val assetTransferAmountPreviewUseCase: AssetTransferAmountPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var assetTransaction = savedStateHandle.getOrThrow<AssetTransaction>(ASSET_TRANSACTION_KEY)
        private set
    private val shouldPopulateAmountWithMax = savedStateHandle.getOrElse(SHOULD_POPULATE_AMOUNT_WITH_MAX, false)

    private val _assetTransferAmountPreviewFlow = MutableStateFlow<AssetTransferAmountPreview?>(null)
    val assetTransferAmountPreviewFlow: StateFlow<AssetTransferAmountPreview?> = _assetTransferAmountPreviewFlow

    init {
        getInitialAssetTransferAmountPreview()
    }

    private fun getInitialAssetTransferAmountPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = with(assetTransaction) {
                with(assetTransferAmountPreviewUseCase.getAssetTransferAmountPreview(senderAddress, assetId)) {
                    if (shouldPopulateAmountWithMax) {
                        copy(onPopulateAmountWithMaxEvent = Event(Unit))
                    } else {
                        this
                    }
                }
            }
            _assetTransferAmountPreviewFlow.emit(result)
        }
    }

    fun updateAssetTransferAmountPreviewAccordingToAmount(amount: BigDecimal) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = with(assetTransaction) {
                assetTransferAmountPreviewUseCase.getAssetTransferAmountPreview(senderAddress, assetId, amount)
            }
            _assetTransferAmountPreviewFlow.emit(result)
        }
    }

    fun onAmountSelected(amount: BigDecimal) {
        val currentPreview = _assetTransferAmountPreviewFlow.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val result = assetTransferAmountPreviewUseCase.getAmountValidatedPreview(currentPreview, amount)
            _assetTransferAmountPreviewFlow.emit(result)
        }
    }

    fun updateUiWithMaxAmount() {
        viewModelScope.launch {
            val currentPreview = _assetTransferAmountPreviewFlow.value ?: return@launch
            val result = assetTransferAmountPreviewUseCase.updatePreviewWithMaximumAmount(
                currentPreview,
                assetTransaction
            )
            _assetTransferAmountPreviewFlow.emit(result)
        }
    }

    fun shouldShowTransactionTips(): Boolean {
        return assetTransferAmountPreviewUseCase.shouldShowTransactionTips()
    }

    fun updateTransactionNotes(lockedNote: String?, transactionNote: String?) {
        assetTransaction = assetTransaction.copy(
            note = transactionNote,
            xnote = lockedNote
        )
    }

    fun sendWithCalculatedSendableAmount() {
        viewModelScope.launch {
            val address = _assetTransferAmountPreviewFlow.value?.senderAddress ?: return@launch
            val assetId = _assetTransferAmountPreviewFlow.value?.assetPreview?.assetId ?: return@launch
            val selectedAmount = _assetTransferAmountPreviewFlow.value?.selectedAmount ?: return@launch
            val amount = assetTransferAmountPreviewUseCase.getCalculatedSendableAmount(address, assetId, selectedAmount)
                ?: return@launch
            _assetTransferAmountPreviewFlow.update {
                it?.copy(sendWithCalculatedSendableAmount = Event(amount))
            }
        }
    }

    fun handleNextNavigation(amount: BigInteger, note: String?, xnote: String?) {
        viewModelScope.launchIO {
            _assetTransferAmountPreviewFlow.value?.run {
                _assetTransferAmountPreviewFlow.update {
                    assetTransferAmountPreviewUseCase.handleNextNavigation(this, assetTransaction, amount, note, xnote)
                }
            }
        }
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
        private const val SHOULD_POPULATE_AMOUNT_WITH_MAX = "shouldPopulateAmountWithMax"
    }
}
