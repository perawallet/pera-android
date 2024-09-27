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
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.BaseRekeyConfirmationViewModel
import com.algorand.android.modules.rekey.baserekeyconfirmation.ui.model.BaseRekeyConfirmationFields
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.model.RekeyToStandardAccountConfirmationPreview
import com.algorand.android.modules.rekey.rekeytostandardaccount.confirmation.ui.usecase.RekeyToStandardAccountConfirmationPreviewUseCase
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

@HiltViewModel
class RekeyToStandardAccountConfirmationViewModel @Inject constructor(
    private val rekeyToStandardAccountConfirmationPreviewUseCase: RekeyToStandardAccountConfirmationPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseRekeyConfirmationViewModel() {

    private val navArgs = RekeyToStandardAccountConfirmationFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress
    val authAccountAddress = navArgs.authAccountAddress

    private val _rekeyToStandardAccountConfirmationPreviewFlow =
        MutableStateFlow<RekeyToStandardAccountConfirmationPreview?>(null)
    override val baseRekeyConfirmationFieldsFlow: StateFlow<BaseRekeyConfirmationFields?>
        get() = _rekeyToStandardAccountConfirmationPreviewFlow

    private var sendTransactionJob: Job? = null

    init {
        initPreview()
    }

    fun onTransactionSigningFailed() {
        _rekeyToStandardAccountConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = false)
        }
    }

    fun onTransactionSigningStarted() {
        _rekeyToStandardAccountConfirmationPreviewFlow.update { preview ->
            preview?.copy(isLoading = true)
        }
    }

    fun sendSignedTransaction(signedTransaction: SignedTransaction) {
        if (sendTransactionJob?.isActive == true) {
            return
        }
        sendTransactionJob = viewModelScope.launchIO {
            rekeyToStandardAccountConfirmationPreviewUseCase.sendSignedTransaction(
                preview = _rekeyToStandardAccountConfirmationPreviewFlow.value ?: return@launchIO,
                signedTransaction = signedTransaction
            ).collectLatest { preview ->
                _rekeyToStandardAccountConfirmationPreviewFlow.value = preview
            }
        }
    }

    fun onConfirmRekeyClick() {
        _rekeyToStandardAccountConfirmationPreviewFlow.update { preview ->
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
            with(rekeyToStandardAccountConfirmationPreviewUseCase) {
                val initialPreview = getInitialRekeyToStandardAccountConfirmationPreview(
                    accountAddress,
                    authAccountAddress
                )
                _rekeyToStandardAccountConfirmationPreviewFlow.value = initialPreview
                updatePreviewWithTransactionFee(initialPreview).collectLatest { preview ->
                    _rekeyToStandardAccountConfirmationPreviewFlow.value = preview
                }
            }
        }
    }
}
