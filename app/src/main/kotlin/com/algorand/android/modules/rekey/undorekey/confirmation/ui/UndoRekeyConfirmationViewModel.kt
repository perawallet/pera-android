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
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.model.UndoRekeyConfirmationPreview
import com.algorand.android.modules.rekey.undorekey.confirmation.ui.usecase.UndoRekeyConfirmationPreviewUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    override val baseRekeyConfirmationFieldsFlow = _undoRekeyConfirmationPreviewFlow.asStateFlow()

    private var sendTransactionJob: Job? = null

    init {
        viewModelScope.launchIO {
            getInitialPreview()
            updatePreviewWithCalculatedTransactionFee()
            updatePreviewWithAccountIcon()
        }
    }

    fun createRekeyToStandardAccountTransaction() {
        viewModelScope.launch {
            val transactionData = undoRekeyConfirmationPreviewUseCase.createUndoRekeyTransaction(accountAddress)
            if (transactionData != null) {
                _undoRekeyConfirmationPreviewFlow.update {
                    it?.copy(onRekeyTransactionDataReady = Event(transactionData))
                }
            }
        }
    }

    fun onTransactionSigningFailed() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            preview?.let {
                undoRekeyConfirmationPreviewUseCase.updatePreviewWithClearLoadingState(preview)
            }
        }
    }

    fun onTransactionSigningStarted() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            preview?.let {
                undoRekeyConfirmationPreviewUseCase.updatePreviewWithLoadingState(preview)
            }
        }
    }

    fun sendRekeyTransaction(transactionDetail: SignedTransactionDetail) {
        if (sendTransactionJob?.isActive == true) {
            return
        }
        sendTransactionJob = viewModelScope.launch(Dispatchers.IO) {
            _undoRekeyConfirmationPreviewFlow.value?.let {
                undoRekeyConfirmationPreviewUseCase.sendUndoRekeyTransaction(
                    transactionDetail = transactionDetail,
                    preview = it
                ).collectLatest { preview ->
                    _undoRekeyConfirmationPreviewFlow.emit(preview)
                }
            }
        }
    }

    fun onConfirmRekeyClick() {
        viewModelScope.launchIO {
            _undoRekeyConfirmationPreviewFlow.update { preview ->
                preview?.let {
                    undoRekeyConfirmationPreviewUseCase.updatePreviewWithRekeyConfirmationClick(
                        accountAddress = accountAddress,
                        preview = it
                    )
                }
            }
        }
    }

    private suspend fun getInitialPreview() {
        _undoRekeyConfirmationPreviewFlow.update {
            undoRekeyConfirmationPreviewUseCase.getInitialUndoRekeyConfirmationPreview(accountAddress)
        }
    }

    private suspend fun updatePreviewWithCalculatedTransactionFee() {
        _undoRekeyConfirmationPreviewFlow.value?.let {
            undoRekeyConfirmationPreviewUseCase.updatePreviewWithTransactionFee(
                preview = it
            ).collectLatest { preview ->
                _undoRekeyConfirmationPreviewFlow.emit(preview)
            }
        }
    }

    private suspend fun updatePreviewWithAccountIcon() {
        _undoRekeyConfirmationPreviewFlow.update { preview ->
            preview?.let {
                undoRekeyConfirmationPreviewUseCase.updatePreviewWithAccountIcon(
                    accountAddress = accountAddress,
                    preview = preview
                )
            }
        }
    }
}
