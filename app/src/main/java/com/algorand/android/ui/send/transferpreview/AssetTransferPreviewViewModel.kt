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

package com.algorand.android.ui.send.transferpreview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.module.foundation.Event
import com.algorand.android.models.AnnotatedString
import com.algorand.android.module.transaction.component.domain.creation.CreateSendTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.send.model.CreateSendTransactionPayload
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import com.algorand.android.module.transaction.ui.sendasset.domain.GetAssetTransferPreview
import com.algorand.android.module.transaction.ui.sendasset.model.AssetTransferPreview
import com.algorand.android.module.transaction.ui.sendasset.model.SendTransactionPayload
import com.algorand.android.utils.Resource
import com.algorand.android.utils.Resource.Error
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AssetTransferPreviewViewModel @Inject constructor(
    private val getAssetTransferPreview: GetAssetTransferPreview,
    private val sendSignedTransaction: SendSignedTransaction,
    private val createSendTransaction: CreateSendTransaction,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var sendAlgoJob: Job? = null

    private val transactionPayload = savedStateHandle.getOrThrow<SendTransactionPayload>(TRANSACTION_DATA_KEY)

    private val _sendAlgoResponseFlow = MutableStateFlow<Event<Resource<String>>?>(null)
    val sendAlgoResponseFlow: StateFlow<Event<Resource<String>>?> = _sendAlgoResponseFlow

    private val _assetTransferPreviewFlow = MutableStateFlow<AssetTransferPreview?>(null)
    val assetTransferPreviewFlow: StateFlow<AssetTransferPreview?> = _assetTransferPreviewFlow

    init {
        getAssetTransferPreview()
    }

    private fun getAssetTransferPreview() {
        viewModelScope.launch {
            val signedTransactionPreview = getAssetTransferPreview(transactionPayload)
            _assetTransferPreviewFlow.emit(signedTransactionPreview)
        }
    }

    fun createAssetTransferTransaction() {
        viewModelScope.launch {
            val transactionResult = createSendTransaction(
                CreateSendTransactionPayload(
                    senderAddress = transactionPayload.senderAddress,
                    receiverAddress = transactionPayload.targetUser.getReceiverAddress(),
                    assetId = transactionPayload.assetId,
                    amount = transactionPayload.amount,
                    note = _assetTransferPreviewFlow.value?.note ?: transactionPayload.note?.note,
                    xnote = transactionPayload.note?.xnote
                )
            ) as? CreateTransactionResult.TransactionCreated
            val transaction = transactionResult?.transaction ?: return@launch
            _assetTransferPreviewFlow.update {
                it?.copy(
                    onSendAssetTransaction = Event(transaction)
                )
            }
        }
    }

    fun sendSignedTxn(signedTransaction: SignedTransaction) {
        if (sendAlgoJob?.isActive == true) {
            return
        }
        sendAlgoJob = viewModelScope.launch {
            _sendAlgoResponseFlow.emit(Event(Resource.Loading))
            sendSignedTransaction(signedTransaction, waitForConfirmation = false).use(
                onSuccess = {
                    _sendAlgoResponseFlow.emit(Event(Resource.Success(it.value)))
                },
                onFailed = { exception, _ ->
                    if (exception.message != null) {
                        _sendAlgoResponseFlow.emit(Event(Error.Api(exception)))
                    } else {
                        val error = Error.GlobalWarning(R.string.error, AnnotatedString(R.string.an_error_occured))
                        _sendAlgoResponseFlow.emit(Event(error))
                    }
                }
            )
        }
    }

    fun onNoteUpdate(newNote: String) {
        viewModelScope.launch {
            if (_assetTransferPreviewFlow.value?.isNoteEditable == true) {
                val newPreview = _assetTransferPreviewFlow.value?.copy(note = newNote)
                _assetTransferPreviewFlow.emit(newPreview)
            }
        }
    }

    companion object {
        private const val TRANSACTION_DATA_KEY = "sendTransactionPayload"
    }
}
