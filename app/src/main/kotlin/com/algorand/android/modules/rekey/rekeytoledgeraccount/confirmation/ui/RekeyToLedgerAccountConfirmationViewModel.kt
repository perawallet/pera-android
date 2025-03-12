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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.model.RekeyToLedgerAccountConfirmationPreview
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.usecase.RekeyToLedgerAccountConfirmationPreviewUseCase
import com.algorand.android.modules.transaction.refactor.usecase.CreateRekeyTransactionData
import com.algorand.android.utils.Event
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RekeyToLedgerAccountConfirmationViewModel @Inject constructor(
    private val rekeyToLedgerAccountConfirmationPreviewUseCase: RekeyToLedgerAccountConfirmationPreviewUseCase,
    private val createRekeyTransactionData: CreateRekeyTransactionData,
    savedStateHandle: SavedStateHandle
) : BaseRekeyConfirmationViewModel() {

    private val navArgs = RekeyToLedgerAccountConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress
    val authAccountAddress = navArgs.authAccountAddress

    private var sendTransactionJob: Job? = null

    private val rekeyToLedgerAccountConfirmationPreviewFlow =
        MutableStateFlow<RekeyToLedgerAccountConfirmationPreview?>(null)
    override val baseRekeyConfirmationFieldsFlow: StateFlow<RekeyToLedgerAccountConfirmationPreview?>
        get() = rekeyToLedgerAccountConfirmationPreviewFlow.asStateFlow()

    init {
        viewModelScope.launchIO {
            getInitialPreview()
            updatePreviewWithCalculatedTransactionFee()
        }
    }

    fun createRekeyToLedgerAccountTransaction() {
        viewModelScope.launch {
            val transactionData = createRekeyTransactionData(accountAddress, authAccountAddress) ?: return@launch
            rekeyToLedgerAccountConfirmationPreviewFlow.update {
                it?.copy(onRekeyTransactionDataReady = Event(transactionData))
            }
        }
    }

    fun onTransactionSigningFailed() {
        rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
            rekeyToLedgerAccountConfirmationPreviewUseCase.updatePreviewWithClearLoadingState(preview ?: return)
        }
    }

    fun onTransactionSigningStarted() {
        rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
            rekeyToLedgerAccountConfirmationPreviewUseCase.updatePreviewWithLoadingState(preview ?: return)
        }
    }

    fun sendRekeyTransaction(transactionDetail: SignedTransactionDetail.RekeyOperation) {
        if (sendTransactionJob?.isActive == true) {
            return
        }
        sendTransactionJob = viewModelScope.launch(Dispatchers.IO) {
            rekeyToLedgerAccountConfirmationPreviewUseCase.sendRekeyToLedgerAccountTransaction(
                transactionDetail = transactionDetail,
                preview = rekeyToLedgerAccountConfirmationPreviewFlow.value ?: return@launch
            ).collectLatest { preview ->
                rekeyToLedgerAccountConfirmationPreviewFlow.emit(preview)
            }
        }
    }

    fun onConfirmRekeyClick() {
        viewModelScope.launch {
            rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
                rekeyToLedgerAccountConfirmationPreviewUseCase.updatePreviewWithRekeyConfirmationClick(
                    accountAddress = accountAddress,
                    preview = preview ?: return@launch
                )
            }
        }
    }

    private suspend fun getInitialPreview() {
        rekeyToLedgerAccountConfirmationPreviewFlow.value = rekeyToLedgerAccountConfirmationPreviewUseCase
            .getInitialRekeyToStandardAccountConfirmationPreview(
                accountAddress = accountAddress,
                authAccountAddress = authAccountAddress
            )
    }

    private suspend fun updatePreviewWithCalculatedTransactionFee() {
        rekeyToLedgerAccountConfirmationPreviewUseCase.updatePreviewWithTransactionFee(
            preview = rekeyToLedgerAccountConfirmationPreviewFlow.value ?: return
        ).collectLatest { preview ->
            rekeyToLedgerAccountConfirmationPreviewFlow.emit(preview)
        }
    }
}
