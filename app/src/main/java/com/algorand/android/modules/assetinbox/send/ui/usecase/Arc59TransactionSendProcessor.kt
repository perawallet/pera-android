/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.modules.assetinbox.send.ui.usecase

import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.transaction.confirmation.domain.usecase.TransactionConfirmationUseCase
import com.algorand.android.usecase.SendSignedTransactionUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

class Arc59TransactionSendProcessor @Inject constructor(
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val transactionConfirmationUseCase: TransactionConfirmationUseCase
) {
    var transactionId: String? = null

    suspend fun sendSignedTransactions(
        transactions: MutableList<SignedTransactionDetail>,
        onSendTransactionsSuccess: suspend (String?) -> Unit,
        onSendTransactionsFailed: suspend (String?) -> Unit
    ) {
        val signedTransaction = transactions.removeFirstOrNull()
        if (signedTransaction == null) {
            onSendTransactionsSuccess(transactionId)
            return
        }
        sendSignedTransactionUseCase.sendSignedTransaction(signedTransaction).collectLatest {
            it.useSuspended(
                onSuccess = { txnId ->
                    transactionId = txnId
                    if (signedTransaction.shouldWaitForConfirmation) {
                        waitForTransactionFormation(
                            txnId = txnId,
                            remainingTransactionsToSend = transactions,
                            onSendTransactionsSuccess = onSendTransactionsSuccess,
                            onSendTransactionsFailed = onSendTransactionsFailed
                        )
                    } else {
                        sendSignedTransactions(
                            transactions = transactions,
                            onSendTransactionsSuccess = onSendTransactionsSuccess,
                            onSendTransactionsFailed = onSendTransactionsFailed
                        )
                    }
                },
                onFailed = { errorDataResource ->
                    onSendTransactionsFailed(errorDataResource.exception?.message.orEmpty())
                }
            )
        }
    }

    private suspend fun waitForTransactionFormation(
        txnId: String,
        remainingTransactionsToSend: MutableList<SignedTransactionDetail>,
        onSendTransactionsSuccess: suspend (String?) -> Unit,
        onSendTransactionsFailed: suspend (String?) -> Unit
    ) {
        transactionConfirmationUseCase.waitForConfirmation(txnId).collectLatest {
            it.useSuspended(
                onSuccess = {
                    sendSignedTransactions(
                        transactions = remainingTransactionsToSend,
                        onSendTransactionsSuccess = onSendTransactionsSuccess,
                        onSendTransactionsFailed = onSendTransactionsFailed
                    )
                },
                onFailed = {
                    onSendTransactionsFailed(it.exception?.message.orEmpty())
                }
            )
        }
    }
}
