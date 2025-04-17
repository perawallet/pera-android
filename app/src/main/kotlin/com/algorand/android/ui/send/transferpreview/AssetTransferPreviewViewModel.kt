/*
 * Copyright 2025 Pera Wallet, LDA
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
import com.algorand.android.core.transaction.TransactionSignManager
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetTransferPreview
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionSignData
import com.algorand.android.usecase.AssetTransferPreviewUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.Resource.Error.GlobalWarning
import com.algorand.android.utils.flatten
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AssetTransferPreviewViewModel @Inject constructor(
    private val assetTransferPreviewUserCase: AssetTransferPreviewUseCase,
    private val transactionSignManager: TransactionSignManager,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAccountInformation: GetAccountInformation,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var sendAlgoJob: Job? = null
    private val transactionData = savedStateHandle.getOrThrow<TransactionSignData.Send>(TRANSACTION_DATA_KEY)

    private val _sendAlgoResponseFlow = MutableStateFlow<Event<Resource<String>>?>(null)
    val sendAlgoResponseFlow: StateFlow<Event<Resource<String>>?> = _sendAlgoResponseFlow

    private val _assetTransferPreviewFlow = MutableStateFlow<AssetTransferPreview?>(null)
    val assetTransferPreviewFlow: StateFlow<AssetTransferPreview?> = _assetTransferPreviewFlow

    private val _signArc59TransactionFlow = MutableStateFlow<TransactionSignData?>(null)
    val signArc59TransactionFlow: StateFlow<TransactionSignData?> = _signArc59TransactionFlow

    private var unsignedArc59Transactions = listOf<TransactionSignData>()
    private val signedArc59Transactions = mutableListOf<SignedTransactionDetail>()

    init {
        getAssetTransferPreview(listOf(transactionData))
        if (transactionData.isArc59Transaction) {
            makeArc59Transactions(transactionData)
        }
    }

    private fun getAssetTransferPreview(
        transactionList: List<TransactionSignData>,
        receiverMinBalanceFee: Long? = null
    ) {
        viewModelScope.launch {
            val signedTransactionPreview = assetTransferPreviewUserCase.getAssetTransferPreview(
                transactionList,
                receiverMinBalanceFee
            )
            _assetTransferPreviewFlow.emit(signedTransactionPreview)
        }
    }

    fun sendSignedTransaction(signedTransactionDetail: SignedTransactionDetail) {
        var signedTransactionDetailCopy: SignedTransactionDetail = signedTransactionDetail
        if (transactionData.isArc59Transaction) {
            signedArc59Transactions.add(signedTransactionDetail)
            if (signedArc59Transactions.size < unsignedArc59Transactions.size) {
                viewModelScope.launch {
                    _signArc59TransactionFlow.emit(unsignedArc59Transactions[signedArc59Transactions.size])
                }
                return
            }
            signedTransactionDetailCopy =
                (signedArc59Transactions.last() as SignedTransactionDetail.Send).copy(
                    signedTransactionData = signedArc59Transactions.map { it.signedTransactionData }
                        .flatten()
                )
        }
        if (sendAlgoJob?.isActive == true) {
            return
        }
        sendAlgoJob = viewModelScope.launch {
            assetTransferPreviewUserCase.sendSignedTransaction(signedTransactionDetailCopy)
                .collectLatest {
                    when (it) {
                        is DataResource.Loading -> _sendAlgoResponseFlow.emit(Event(Resource.Loading))
                        is DataResource.Error -> {
                            if (it.exception != null) {
                                _sendAlgoResponseFlow.emit(Event(Resource.Error.Api(it.exception!!)))
                            } else {
                                _sendAlgoResponseFlow.emit(
                                    Event(GlobalWarning(R.string.error, AnnotatedString(R.string.an_error_occured)))
                                )
                            }
                        }

                        is DataResource.Success -> _sendAlgoResponseFlow.emit(Event(Resource.Success(it.data)))
                    }
                }
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

    private fun makeArc59Transactions(transactionData: TransactionSignData) {
        viewModelScope.launch {
            (transactionData as TransactionSignData.Send).let {
                val transactions = transactionSignManager.createArc59SendTransactionList(transactionData)
                val arc59Transactions = mutableListOf<TransactionSignData>()
                transactions?.forEach { transaction ->
                    if (transaction.accountAddress == transactionData.senderAccountAddress) {
                        arc59Transactions.add(
                            transactionData.copy(transactionByteArray = transaction.transactionByteArray)
                        )
                    } else {
                        val targetAccountInfo = getAccountInformation(transactionData.targetUser.publicKey)
                        arc59Transactions.add(
                            TransactionSignData.AddAsset(
                                senderAccountAddress = transactionData.targetUser.publicKey,
                                senderAuthAddress = targetAccountInfo?.rekeyAdminAddress,
                                assetId = transactionData.assetId,
                                transactionByteArray = transaction.transactionByteArray,
                                isArc59Transaction = transactionData.isArc59Transaction,
                                signer = getTransactionSigner(transactionData.targetUser.publicKey)
                            )
                        )
                    }
                }
                signedArc59Transactions.clear()
                unsignedArc59Transactions = arc59Transactions
                val receiverMinBalanceFee = transactionSignManager.getReceiverMinBalanceFee(transactionData)
                getAssetTransferPreview(unsignedArc59Transactions, receiverMinBalanceFee)
            }
        }
    }

    fun sendArc59Transactions() {
        viewModelScope.launch {
            _signArc59TransactionFlow.emit(unsignedArc59Transactions.first())
        }
    }

    fun getTransactionData(): TransactionSignData.Send {
        return if (_assetTransferPreviewFlow.value?.isNoteEditable == true) {
            transactionData.copy(
                note = _assetTransferPreviewFlow.value?.note,
                xnote = null
            )
        } else {
            transactionData.copy(
                note = null,
                xnote = _assetTransferPreviewFlow.value?.note
            )
        }
    }

    companion object {
        private const val TRANSACTION_DATA_KEY = "transactionData"
    }
}
