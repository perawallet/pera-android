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

package com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.model.BaseRekeyConfirmationFields
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.model.RekeyToStandardAccountConfirmationPreview
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.usecase.RekeyToStandardAccountConfirmationPreviewUseCase
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionData
import com.algorand.android.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RekeyToStandardAccountConfirmationViewModel @Inject constructor(
    private val previewUseCase: RekeyToStandardAccountConfirmationPreviewUseCase,
    private val createRekeyTransactionData: CreateRekeyTransactionData,
    savedStateHandle: SavedStateHandle
) : BaseRekeyConfirmationViewModel() {

    private val navArgs = RekeyToStandardAccountConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress
    val authAccountAddress = navArgs.authAccountAddress

    private val _previewFlow = MutableStateFlow<RekeyToStandardAccountConfirmationPreview?>(null)
    override val baseRekeyConfirmationFieldsFlow: StateFlow<BaseRekeyConfirmationFields?>
        get() = _previewFlow

    private var sendTransactionJob: Job? = null

    init {
        initPreview()
    }

    fun createRekeyToStandardAccountTransaction() {
        viewModelScope.launch {
            val transactionData = createRekeyTransactionData(accountAddress, authAccountAddress)
            if (transactionData != null) {
                _previewFlow.update {
                    it?.copy(
                        onRekeyTransactionDataReady = Event(transactionData),
                    )
                }
            }
        }
    }

    fun onTransactionSigningFailed() {
        _previewFlow.update { preview ->
            preview?.run {
                previewUseCase.updatePreviewWithClearLoadingState(preview)
            }
        }
    }

    fun onTransactionSigningStarted() {
        _previewFlow.update { preview ->
            preview?.run {
                previewUseCase.updatePreviewWithLoadingState(preview)
            }
        }
    }

    fun sendRekeyTransaction(transactionDetail: SignedTransactionDetail.RekeyOperation) {
        if (sendTransactionJob?.isActive == true) {
            return
        }
        sendTransactionJob = viewModelScope.launch(Dispatchers.IO) {
            val currentPreview = _previewFlow.value ?: return@launch
            previewUseCase.sendRekeyToStandardAccountTransaction(
                transactionDetail = transactionDetail,
                preview = currentPreview
            ).collectLatest { preview ->
                _previewFlow.emit(preview)
            }
        }
    }

    fun onConfirmRekeyClick() {
        viewModelScope.launch {
            _previewFlow.update { preview ->
                preview?.run {
                    previewUseCase.updatePreviewWithRekeyConfirmationClick(
                        accountAddress = accountAddress,
                        preview = preview
                    )
                }
            }
        }
    }

    private fun initPreview() {
        viewModelScope.launch {
            val initialPreview = previewUseCase.getInitialRekeyToStandardAccountConfirmationPreview(
                accountAddress = accountAddress,
                authAccountAddress = authAccountAddress
            )
            _previewFlow.value = initialPreview
            previewUseCase.updatePreviewWithTransactionFee(preview = initialPreview).collectLatest { preview ->
                _previewFlow.emit(preview)
            }
        }
    }
}
