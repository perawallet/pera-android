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
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.model.RekeyToLedgerAccountConfirmationPreview
import com.algorand.android.modules.rekey.rekeytoledgeraccount.confirmation.ui.usecase.RekeyToLedgerAccountConfirmationPreviewUseCase
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

@HiltViewModel
class RekeyToLedgerAccountConfirmationViewModel @Inject constructor(
    private val rekeyToLedgerAccountConfirmationPreviewUseCase: RekeyToLedgerAccountConfirmationPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseRekeyConfirmationViewModel() {

    private val navArgs = RekeyToLedgerAccountConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress
    val selectedLedgerAuthAccount = navArgs.selectedLedgerAccount

    private var sendTransactionJob: Job? = null

    private val rekeyToLedgerAccountConfirmationPreviewFlow =
        MutableStateFlow<RekeyToLedgerAccountConfirmationPreview?>(null)
    override val baseRekeyConfirmationFieldsFlow: StateFlow<RekeyToLedgerAccountConfirmationPreview?>
        get() = rekeyToLedgerAccountConfirmationPreviewFlow

    init {
        initPreview()
    }

    fun onTransactionSigningFailed() {
        rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = false)
        }
    }

    fun onTransactionSigningStarted() {
        rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = true)
        }
    }

    fun sendRekeyTransaction(signedTransaction: SignedTransaction) {
        if (sendTransactionJob?.isActive == true) {
            return
        }
        sendTransactionJob = viewModelScope.launchIO {
            rekeyToLedgerAccountConfirmationPreviewUseCase.sendRekeyToLedgerAccountTransaction(
                preview = rekeyToLedgerAccountConfirmationPreviewFlow.value ?: return@launchIO,
                signedTransaction = signedTransaction
            ).collectLatest { preview ->
                rekeyToLedgerAccountConfirmationPreviewFlow.emit(preview)
            }
        }
    }

    fun onConfirmRekeyClick() {
        rekeyToLedgerAccountConfirmationPreviewFlow.update { preview ->
            val isRekeyed = preview?.accountInformation?.isRekeyed() ?: return@update preview
            if (isRekeyed) {
                preview.copy(navToRekeyedAccountConfirmationBottomSheetEvent = Event(Unit))
            } else {
                preview.copy(onSendTransactionEvent = Event(Unit))
            }
        }
    }

    private fun initPreview() {
        viewModelScope.launchIO {
            with(rekeyToLedgerAccountConfirmationPreviewUseCase) {
                val initialPreview = getInitialRekeyToStandardAccountConfirmationPreview(
                    accountAddress = accountAddress,
                    authAccountAddress = selectedLedgerAuthAccount.address
                )
                rekeyToLedgerAccountConfirmationPreviewFlow.value = initialPreview
                updatePreviewWithTransactionFee(initialPreview).collectLatest { preview ->
                    rekeyToLedgerAccountConfirmationPreviewFlow.value = preview
                }
            }
        }
    }
}
