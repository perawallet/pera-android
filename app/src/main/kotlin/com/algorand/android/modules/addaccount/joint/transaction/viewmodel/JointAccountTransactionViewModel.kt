/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.addaccount.joint.transaction.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequest
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionPreview
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionPreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JointAccountTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getJointAccountTransactionPreview: GetJointAccountTransactionPreview,
    private val declineJointAccountSignRequest: DeclineJointAccountSignRequest,
    private val signAndSubmitJointAccountSignature: SignAndSubmitJointAccountSignature,
    private val refreshInboxCache: RefreshInboxCache,
    private val processor: JointAccountTransactionProcessor
) : ViewModel(),
    StateViewModel<JointAccountTransactionViewModel.ViewState> by stateDelegate,
    EventViewModel<JointAccountTransactionViewModel.ViewEvent> by eventDelegate {

    private val signRequestId: String? = savedStateHandle.get<String>("signRequestId")

    init {
        stateDelegate.setDefaultState(ViewState.Loading)
        initializeViewModel()
    }

    fun onConfirmTransaction() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val validationResult = processor.validateConfirmTransaction(contentState.preview, signRequestId)
            if (validationResult == null) {
                emitError(R.string.an_error_occurred)
                return@onState
            }
            viewModelScope.launch {
                stateDelegate.updateState { ViewState.Loading }
                processConfirmTransaction(validationResult)
            }
        }
    }

    fun onCancel() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val updatedPreview = contentState.preview.copy(
                transactionState = JointAccountTransactionState.Canceled
            )
            stateDelegate.updateState { ViewState.Content(updatedPreview) }
        }
    }

    fun declineSignRequest() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val requestId = signRequestId ?: return@onState emitError(R.string.an_error_occurred)
            val participantAddress = processor.findDeclineParticipantAddress(contentState.preview)
                ?: return@onState emitError(R.string.an_error_occurred)

            viewModelScope.launch {
                stateDelegate.updateState { ViewState.Loading }
                executeDeclineRequest(requestId, participantAddress, contentState.preview)
            }
        }
    }

    fun onLedgerSignSuccess() {
        viewModelScope.launch {
            refreshInboxCache()
            loadTransactionPreview()
            handleLedgerSignSuccessAction()
        }
    }

    fun onLedgerSignError(errorMessageResId: Int) {
        emitError(errorMessageResId)
    }

    fun onCopyAddressClick() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            viewModelScope.launch {
                eventDelegate.sendEvent(ViewEvent.CopyAddress(contentState.preview.recipientAddress))
            }
        }
    }

    private fun initializeViewModel() {
        if (!signRequestId.isNullOrBlank()) {
            loadTransactionPreview()
        } else {
            Log.e(TAG, "signRequestId is null or blank")
            emitError(R.string.an_error_occurred)
            emitNavigateBack()
        }
    }

    private suspend fun processConfirmTransaction(data: JointAccountTransactionProcessor.ConfirmTransactionData) {
        val signedAddresses = signLocalAccounts(data)
        if (signedAddresses.isNotEmpty()) refreshInboxCache()

        val updatedPreview = processor.createUpdatedPreviewAfterSigning(data.preview, signedAddresses)
        stateDelegate.updateState { ViewState.Content(updatedPreview) }

        handlePostSigningAction(processor.determinePostSigningAction(data, updatedPreview, signRequestId))
    }

    private suspend fun signLocalAccounts(
        data: JointAccountTransactionProcessor.ConfirmTransactionData
    ): List<String> {
        if (!data.hasUnsignedLocalAccounts) return emptyList()

        val signedAddresses = mutableListOf<String>()
        data.preview.unsignedLocalParticipantAddresses.forEach { participantAddress ->
            signAndSubmitJointAccountSignature(
                signRequestId = data.requestId,
                participantAddress = participantAddress,
                rawTransactions = data.preview.rawTransactions
            ).use(
                onSuccess = { signedAddresses.add(participantAddress) },
                onFailed = { _, _ -> }
            )
        }
        return signedAddresses
    }

    private suspend fun executeDeclineRequest(
        requestId: String,
        participantAddress: String,
        preview: JointAccountTransactionPreview
    ) {
        declineJointAccountSignRequest(requestId, participantAddress).use(
            onSuccess = {
                refreshInboxCache()
                emitNavigateBack()
            },
            onFailed = { _, _ ->
                stateDelegate.updateState { ViewState.Content(preview) }
                emitError(R.string.an_error_occurred)
            }
        )
    }

    private fun loadTransactionPreview() {
        viewModelScope.launch {
            stateDelegate.updateState { ViewState.Loading }
            getJointAccountTransactionPreview(signRequestId!!).use(
                onSuccess = { preview ->
                    val updatedPreview = processor.processLoadedPreview(preview)
                    stateDelegate.updateState { ViewState.Content(updatedPreview) }
                    if (updatedPreview.shouldShowPendingSignaturesDirectly) {
                        eventDelegate.sendEvent(ViewEvent.ShowPendingSignaturesDirectly)
                    }
                },
                onFailed = { exception, code ->
                    Log.e(TAG, "Failed to load preview: code=$code, exception=$exception")
                    stateDelegate.updateState { ViewState.Error(R.string.sign_request_not_available) }
                }
            )
        }
    }

    private fun handleLedgerSignSuccessAction() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val action = processor.determineLedgerSuccessAction(contentState.preview, signRequestId)
            viewModelScope.launch { handlePostSigningAction(action) }
        }
    }

    private suspend fun handlePostSigningAction(action: JointAccountTransactionProcessor.PostSigningAction) {
        when (action) {
            is JointAccountTransactionProcessor.PostSigningAction.TriggerLedgerSigning -> {
                eventDelegate.sendEvent(ViewEvent.StartLedgerSigning(action.data))
            }
            is JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures -> {
                eventDelegate.sendEvent(ViewEvent.ShowPendingSignaturesBottomSheet)
            }
        }
    }

    private fun emitError(errorMessageResId: Int) {
        viewModelScope.launch { eventDelegate.sendEvent(ViewEvent.ShowError(errorMessageResId)) }
    }

    private fun emitNavigateBack() {
        viewModelScope.launch { eventDelegate.sendEvent(ViewEvent.NavigateBack) }
    }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Content(val preview: JointAccountTransactionPreview) : ViewState
        data class Error(val messageResId: Int) : ViewState
    }

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
        data class ShowError(val messageResId: Int) : ViewEvent
        data object ShowPendingSignaturesBottomSheet : ViewEvent
        data object ShowPendingSignaturesDirectly : ViewEvent
        data class StartLedgerSigning(val data: JointAccountTransactionProcessor.LedgerSignData) : ViewEvent
        data class CopyAddress(val address: String) : ViewEvent
    }

    companion object {
        private const val TAG = "JointAccountTxVM"
    }
}
