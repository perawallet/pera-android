/*
 * Copyright 2022-2025 Pera Wallet, LDA
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
import com.algorand.android.ui.send.transferpreview.AssetTransferPreviewViewModel.ViewEvent
import com.algorand.android.usecase.AssetTransferPreviewUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.Resource
import com.algorand.android.utils.Resource.Error.GlobalWarning
import com.algorand.android.utils.flatten
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AssetTransferPreviewViewModel @Inject constructor(
    private val assetTransferPreviewUseCase: AssetTransferPreviewUseCase,
    private val transactionSignManager: TransactionSignManager,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    private val eventDelegate: EventDelegate<ViewEvent>,
    savedStateHandle: SavedStateHandle
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

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
        if (transactionData.isArc59Transaction) {
            makeArc59Transactions(transactionData)
        } else {
            initPreview(listOf(transactionData), isConfirmButtonEnabled = true)
        }
    }

    private fun initPreview(
        transactionList: List<TransactionSignData>,
        receiverMinBalanceFee: Long? = null,
        isConfirmButtonEnabled: Boolean
    ) {
        viewModelScope.launch {
            val signedTransactionPreview = assetTransferPreviewUseCase.getAssetTransferPreview(
                transactionList,
                receiverMinBalanceFee
            ).copy(isConfirmButtonEnabled = isConfirmButtonEnabled)
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
            assetTransferPreviewUseCase.sendSignedTransaction(signedTransactionDetailCopy)
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
                val receiverMinBalanceFee = transactionSignManager.getReceiverMinBalanceFee(transactionData)
                initPreview(listOf(transactionData), receiverMinBalanceFee, isConfirmButtonEnabled = false)
                if (canSenderCoverRequiredFee(it)) {
                    val arc59Transactions = createArc59SendTransactions(transactionData)
                    signedArc59Transactions.clear()
                    unsignedArc59Transactions = arc59Transactions
                    initPreview(unsignedArc59Transactions, receiverMinBalanceFee, isConfirmButtonEnabled = true)
                } else {
                    eventDelegate.sendEvent(ViewEvent.ShowInsufficientBalanceError(it.minimumBalance))
                    initPreview(listOf(transactionData), receiverMinBalanceFee, isConfirmButtonEnabled = false)
                }
            }
        }
    }

    private suspend fun canSenderCoverRequiredFee(transactionData: TransactionSignData.Send): Boolean {
        val transactionFee = getTransactionFee(transactionData)
        val requiredMinSenderBalance = transactionData.minimumBalance + transactionFee
        return transactionData.senderAlgoAmount.isGreaterThan(requiredMinSenderBalance.toBigInteger())
    }

    @Suppress("MagicNumber")
    private suspend fun getTransactionFee(transactionData: TransactionSignData.Send): Long {
        /**
         * To be able to calculate transaction fees, we need to create transaction.
         * To be able to create transactions, we need to make sure that the sender has enough balance.
         * Otherwise SDK throws exception. This transaction is being used to calculate the fee only, not to send.
         */
        val dummyTransactionData = transactionData.copy(senderAlgoAmount = BigInteger.valueOf(999_999_999L))
        val arc59Transactions = createArc59SendTransactions(dummyTransactionData)
        val receiverMinBalanceFee = transactionSignManager.getReceiverMinBalanceFee(transactionData)
        return assetTransferPreviewUseCase.getTotalTxnFee(arc59Transactions, receiverMinBalanceFee)
    }

    private suspend fun createArc59SendTransactions(
        transactionData: TransactionSignData.Send
    ): List<TransactionSignData> {
        val transactions = transactionSignManager.createArc59SendTransactionList(transactionData)
        return transactions?.map { transaction ->
            if (transaction.accountAddress == transactionData.senderAccountAddress) {
                transactionData.copy(transactionByteArray = transaction.transactionByteArray)
            } else {
                TransactionSignData.AddAsset(
                    senderAccountAddress = transactionData.targetUser.publicKey,
                    senderAuthAddress = getAccountRekeyAdminAddress(transactionData.targetUser.publicKey),
                    assetId = transactionData.assetId,
                    transactionByteArray = transaction.transactionByteArray,
                    isArc59Transaction = transactionData.isArc59Transaction,
                    signer = getTransactionSigner(transactionData.targetUser.publicKey)
                )
            }
        }.orEmpty()
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

    sealed interface ViewEvent {
        data class ShowInsufficientBalanceError(val requiredMinBalance: Long) : ViewEvent
    }

    companion object {
        private const val TRANSACTION_DATA_KEY = "transactionData"
    }
}
