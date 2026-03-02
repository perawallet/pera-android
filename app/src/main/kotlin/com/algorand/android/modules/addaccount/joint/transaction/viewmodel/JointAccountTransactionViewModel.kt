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
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionViewState
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class JointAccountTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val getJointAccountTransactionViewState: GetJointAccountTransactionViewState,
    private val declineJointAccountSignRequest: DeclineJointAccountSignRequest,
    private val signAndSubmitJointAccountSignature: SignAndSubmitJointAccountSignature,
    private val refreshInboxCache: RefreshInboxCache,
    private val getInboxMessagesFlow: GetInboxMessagesFlow,
    private val processor: JointAccountTransactionProcessor
) : ViewModel(),
    StateViewModel<JointAccountTransactionViewModel.ViewState> by stateDelegate,
    EventViewModel<JointAccountTransactionViewModel.ViewEvent> by eventDelegate {

    private val signRequestId: String? = savedStateHandle.get<String>("signRequestId")
    private val isSilentRefreshInProgress = AtomicBoolean(false)

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
            val updatedSigners = contentState.preview.signerAccounts.map { signer ->
                if (signer.signatureStatus == JointAccountSignatureStatus.Pending) {
                    signer.copy(showProgress = false)
                } else {
                    signer
                }
            }
            val updatedPreview = contentState.preview.copy(
                transactionState = JointAccountTransactionState.Canceled,
                signerAccounts = updatedSigners
            )
            stateDelegate.updateState { ViewState.Content(updatedPreview) }
        }
    }

    fun declineSignRequest() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            val requestId = signRequestId ?: return@onState emitError(R.string.an_error_occurred)
            val participantAddresses = processor.findDeclineParticipantAddresses(contentState.preview)
            if (participantAddresses.isEmpty()) return@onState emitError(R.string.an_error_occurred)

            viewModelScope.launch {
                stateDelegate.updateState { ViewState.Loading }
                executeDeclineRequest(requestId, participantAddresses, contentState.preview)
            }
        }
    }

    fun onLedgerSignSuccess() {
        viewModelScope.launch {
            refreshInboxCache()
            silentRefreshPreview()
        }
    }

    fun onLedgerSignError(errorMessageResId: Int) {
        emitError(errorMessageResId)
    }

    fun onSignLedgerAccount(signer: JointAccountSignerItem) {
        if (!signer.canSignWithLedger) return
        val requestId = signRequestId ?: return
        val bluetoothAddress = signer.ledgerBluetoothAddress ?: return
        val accountIndex = signer.ledgerAccountIndex ?: return

        stateDelegate.onState<ViewState.Content> { contentState ->
            val preview = contentState.preview
            if (preview.rawTransactions.isEmpty()) return@onState

            val ledgerData = JointAccountTransactionProcessor.LedgerSignData(
                signRequestId = requestId,
                accountAddress = signer.accountAddress,
                rawTransactions = preview.rawTransactions,
                ledgerBluetoothAddress = bluetoothAddress,
                ledgerAccountIndex = accountIndex,
                accountAuthAddress = signer.accountAuthAddress,
                isRekeyedToAnotherAccount = signer.accountAuthAddress != null
            )
            viewModelScope.launch {
                eventDelegate.sendEvent(ViewEvent.StartLedgerSigning(ledgerData))
            }
        }
    }

    fun onCopyAddressClick() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            viewModelScope.launch {
                eventDelegate.sendEvent(ViewEvent.CopyAddress(contentState.preview.recipientAddress))
            }
        }
    }

    fun onShowTransactionDetailsClick() {
        val requestId = signRequestId ?: return
        viewModelScope.launch {
            eventDelegate.sendEvent(
                ViewEvent.ShowPendingSignaturesBottomSheet(requestId, isDismissable = true)
            )
        }
    }

    private fun initializeViewModel() {
        if (!signRequestId.isNullOrBlank()) {
            loadTransactionPreview()
            startInboxPollObserver()
        } else {
            Log.e(TAG, "signRequestId is null or blank")
            emitError(R.string.an_error_occurred)
            emitNavigateBack()
        }
    }

    private fun startInboxPollObserver() {
        viewModelScope.launch {
            getInboxMessagesFlow().drop(1).collectLatest {
                stateDelegate.onState<ViewState.Content> { content ->
                    val state = content.preview.transactionState
                    if (state != JointAccountTransactionState.Completed &&
                        state != JointAccountTransactionState.Canceled
                    ) {
                        loadTransactionPreview(silentRefresh = true)
                    }
                }
            }
        }
    }

    private suspend fun processConfirmTransaction(data: JointAccountTransactionProcessor.ConfirmTransactionData) {
        val signedAddresses = signLocalAccounts(data)
        if (signedAddresses.isNotEmpty()) refreshInboxCache()

        val updatedPreview = processor.createUpdatedPreviewAfterSigning(data.preview, signedAddresses)
        stateDelegate.updateState { ViewState.Content(updatedPreview) }

        handlePostSigningAction(processor.determinePostSigningAction(data, updatedPreview, signRequestId))
        loadTransactionPreview(silentRefresh = true)
    }

    private suspend fun signLocalAccounts(
        data: JointAccountTransactionProcessor.ConfirmTransactionData
    ): List<String> {
        if (!data.hasUnsignedLocalAccounts) return emptyList()

        val result = signAndSubmitJointAccountSignature(
            signRequestId = data.requestId,
            participantAddresses = data.preview.unsignedLocalParticipantAddresses,
            rawTransactions = data.preview.rawTransactions
        )
        when (result.apiResult) {
            null -> {
                Log.e(TAG, "signLocalAccounts: apiResult is null")
                emitError(R.string.an_error_occurred)
                return emptyList()
            }

            is PeraResult.Error -> {
                Log.e(TAG, "signLocalAccounts failed: ${result.apiResult}")
                emitError(R.string.an_error_occurred)
                return emptyList()
            }

            is PeraResult.Success -> Unit
        }
        return result.signedAddresses
    }

    private suspend fun executeDeclineRequest(
        requestId: String,
        participantAddresses: List<String>,
        preview: JointAccountTransactionViewState
    ) {
        val declinedAddressSet = participantAddresses.toSet()
        declineJointAccountSignRequest(requestId, participantAddresses).use(
            onSuccess = {
                refreshInboxCache()
                val updatedSigners = preview.signerAccounts.map { signer ->
                    if (signer.accountAddress in declinedAddressSet) {
                        signer.copy(signatureStatus = JointAccountSignatureStatus.Declined)
                    } else {
                        signer
                    }
                }
                val canceledPreview = processor.processLoadedPreview(
                    preview.copy(
                        transactionState = JointAccountTransactionState.Canceled,
                        signerAccounts = updatedSigners
                    )
                )
                stateDelegate.updateState { ViewState.Content(canceledPreview) }
                eventDelegate.sendEvent(
                    ViewEvent.ShowPendingSignaturesBottomSheet(requestId, isDismissable = false)
                )
            },
            onFailed = { _, _ ->
                stateDelegate.updateState { ViewState.Content(preview) }
                emitError(R.string.an_error_occurred)
            }
        )
    }

    private fun loadTransactionPreview(silentRefresh: Boolean = false) {
        viewModelScope.launch {
            if (silentRefresh) {
                silentRefreshPreview()
            } else {
                loadTransactionPreviewWithLoading()
            }
        }
    }

    private suspend fun loadTransactionPreviewWithLoading() {
        val requestId = signRequestId
        if (requestId.isNullOrBlank()) {
            Log.e(TAG, "signRequestId is null or blank during preview load")
            emitError(R.string.an_error_occurred)
            emitNavigateBack()
            return
        }
        stateDelegate.updateState { ViewState.Loading }
        getJointAccountTransactionViewState(requestId).use(
            onSuccess = { preview ->
                val updatedPreview = processor.processLoadedPreview(preview)
                stateDelegate.updateState { ViewState.Content(updatedPreview) }
                if (updatedPreview.shouldShowPendingSignaturesDirectly) {
                    eventDelegate.sendEvent(
                        ViewEvent.ShowPendingSignaturesBottomSheet(requestId, isDismissable = false)
                    )
                }
            },
            onFailed = { exception, code ->
                Log.e(TAG, "Failed to load preview: code=$code, exception=$exception")
                stateDelegate.updateState { ViewState.Error(R.string.sign_request_not_available) }
            }
        )
    }

    private suspend fun silentRefreshPreview() {
        val requestId = signRequestId ?: return
        if (!isSilentRefreshInProgress.compareAndSet(false, true)) return
        try {
            getJointAccountTransactionViewState(requestId).use(
                onSuccess = { preview ->
                    val updatedPreview = processor.processLoadedPreview(preview)
                    stateDelegate.updateState { ViewState.Content(updatedPreview) }
                },
                onFailed = { _, _ -> }
            )
        } finally {
            isSilentRefreshInProgress.set(false)
        }
    }

    private suspend fun handlePostSigningAction(action: JointAccountTransactionProcessor.PostSigningAction) {
        when (action) {
            is JointAccountTransactionProcessor.PostSigningAction.TriggerLedgerSigning -> {
                eventDelegate.sendEvent(ViewEvent.StartLedgerSigning(action.data))
            }

            is JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures -> {
                val requestId = signRequestId ?: return
                eventDelegate.sendEvent(
                    ViewEvent.ShowPendingSignaturesBottomSheet(requestId, isDismissable = false)
                )
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
        data class Content(val preview: JointAccountTransactionViewState) : ViewState
        data class Error(val messageResId: Int) : ViewState
    }

    sealed interface ViewEvent {
        data object NavigateBack : ViewEvent
        data class ShowSuccessAndNavigateBack(val messageResId: Int) : ViewEvent
        data class ShowError(val messageResId: Int) : ViewEvent
        data class ShowPendingSignaturesBottomSheet(
            val signRequestId: String,
            val isDismissable: Boolean
        ) : ViewEvent
        data class StartLedgerSigning(val data: JointAccountTransactionProcessor.LedgerSignData) : ViewEvent
        data class CopyAddress(val address: String) : ViewEvent
    }

    companion object {
        private const val TAG = "JointAccountTxVM"
    }
}
