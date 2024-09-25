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

package com.algorand.android.modules.rekey.undorekey.confirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.CoroutineExtensions.launchIfInactive
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.model.BaseRekeyConfirmationFields
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.usecase.UndoRekeyConfirmationPreviewUseCase
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UndoRekeyConfirmationViewModel @Inject constructor(
    private val undoRekeyConfirmationPreviewUseCase: UndoRekeyConfirmationPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseRekeyConfirmationViewModel() {

    private val navArgs = UndoRekeyConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress

    private val _undoRekeyConfirmationPreviewFlow = MutableStateFlow<UndoRekeyConfirmationPreview?>(null)
    override val baseRekeyConfirmationFieldsFlow: StateFlow<BaseRekeyConfirmationFields?>
        get() = _undoRekeyConfirmationPreviewFlow

    private var sendTransactionJob: Job? = null

    init {
        initPreview()
    }

    fun onTransactionSigningFailed() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = false)
        }
    }

    fun onTransactionSigningStarted() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = true)
        }
    }

    fun sendRekeyTransaction(signedTransaction: SignedTransaction) {
        sendTransactionJob = viewModelScope.launchIfInactive(sendTransactionJob) {
            undoRekeyConfirmationPreviewUseCase.sendSignedTransaction(
                preview = _undoRekeyConfirmationPreviewFlow.value ?: return@launchIfInactive,
                signedTransaction = signedTransaction
            ).collectLatest { transactionSendingResult ->
                _undoRekeyConfirmationPreviewFlow.value = transactionSendingResult
            }
        }
    }

    fun onConfirmRekeyClick() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            if (preview?.accountInformation?.isRekeyed() == true) {
                preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
            } else {
                preview?.copy(onSendTransactionEvent = Event(Unit))
            }
        }
    }

    fun getAccountAuthAddress(): String {
        return _undoRekeyConfirmationPreviewFlow.value?.authAccountDisplayName?.accountAddress.orEmpty()
    }

    private fun initPreview() {
        viewModelScope.launch {
            val preview = undoRekeyConfirmationPreviewUseCase.getInitialUndoRekeyConfirmationPreview(accountAddress)
            _undoRekeyConfirmationPreviewFlow.emit(preview)
            updatePreviewWithCalculatedTransactionFee()
        }
    }

    private suspend fun updatePreviewWithCalculatedTransactionFee() {
        _undoRekeyConfirmationPreviewFlow.value?.run {
            undoRekeyConfirmationPreviewUseCase.updatePreviewWithTransactionFee(this).collectLatest { preview ->
                _undoRekeyConfirmationPreviewFlow.emit(preview)
            }
        }
    }
}
