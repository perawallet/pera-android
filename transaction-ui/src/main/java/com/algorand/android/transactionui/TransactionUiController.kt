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

package com.algorand.android.transactionui

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.lifecycle.collectOnLifecycle
import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult
import com.algorand.android.transaction.domain.model.SignedTransaction
import com.algorand.android.transaction.domain.sign.model.SignTransactionResult
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

class TransactionUiController(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: TransactionUiListener
) {

    private val signResultObserver: suspend (Event<SignTransactionResult>?) -> Unit = { signTransactionResultEvent ->
        signTransactionResultEvent?.consume()?.let { result -> processSignTransactionResult(result) }
    }

    private val createTransactionResult: suspend (CreateTransactionResult) -> Unit = { createTransactionResult ->
        processCreateTransactionResult(createTransactionResult)
    }

    fun setup(
        signResultFlow: Flow<Event<SignTransactionResult>?>,
        createTransactionResultFlow: Flow<CreateTransactionResult>
    ) {
        initObservers(signResultFlow, createTransactionResultFlow)
    }

    private fun initObservers(
        signResultFlow: Flow<Event<SignTransactionResult>?>,
        createTransactionResultFlow: Flow<CreateTransactionResult>
    ) {
        lifecycleOwner.collectOnLifecycle(signResultFlow, signResultObserver)
        lifecycleOwner.collectOnLifecycle(createTransactionResultFlow, createTransactionResult)
    }

    private fun processSignTransactionResult(signTransactionResult: SignTransactionResult) {
        when (signTransactionResult) {
            is SignTransactionResult.TransactionSigned -> {
                listener.onTransactionSigned(signTransactionResult.signedTransaction)
            }
            else -> Log.e("test", "result = $signTransactionResult")
        }
    }

    private fun processCreateTransactionResult(result: CreateTransactionResult) {
        when (result) {
//            CreateTransactionResult.Idle -> Unit
//            CreateTransactionResult.Loading -> showLoading()
//            is CreateTransactionResult.AccountNotFound -> {
//                hideLoading()
//                showGenericError()
//            }
//            is CreateTransactionResult.MinBalanceViolated -> {
//                hideLoading()
//                processMinBalanceViolatedResult(result.requiredBalance)
//            }
//            is CreateTransactionResult.NetworkError -> {
//                hideLoading()
//                showNetworkError()
//            }
            is CreateTransactionResult.TransactionCreated -> listener.onTransactionCreated(result.transaction)
            else -> Log.e("test", "result = $result")
        }
    }

    private fun showGenericError() {
        listener.onError("generic error title", "generic error message")
    }

    private fun showNetworkError() {
        listener.onError("network error title", "network error message")
        /*
        when (val result = transactionsRepository.getTransactionParams()) {
            is Result.Success -> {
                transactionParams = result.data
            }
            is Result.Error -> {
                transactionParams = null
                when (result.exception.cause) {
                    is ConnectException, is SocketException -> {
                        postResult(Defined(AnnotatedString(R.string.the_internet_connection)))
                    }
                    else -> {
                        when (transactionData) {
                            is TransactionData.Send -> {
                                postResult(
                                    TransactionManagerResult.Error.GlobalWarningError.Api(
                                        result.exception.message.orEmpty()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
         */
    }

    private fun processMinBalanceViolatedResult(requiredBalance: BigInteger) {
        // TODO FORMAT MIN BALANCE
        listener.onMinBalanceViolated(requiredBalance.toString())
    }

    private fun showLoading() {
        listener.onLoading()
    }

    private fun hideLoading() {
        listener.onLoadingFinished()
    }

    private fun hideLedgerLoadingDialog() {
        listener.onLedgerLoadingFinished()
    }

    private fun showLedgerLoadingDialog(ledgerName: String?) {
        listener.onLedgerLoading(ledgerName)
    }

    interface TransactionUiListener {
        fun onLoading()
        fun onLoadingFinished()
        fun onMinBalanceViolated(formattedRequiredMinBalance: String)
        fun onError(title: String, message: String)
        fun onTransactionCreated(transaction: Transaction)
        fun onTransactionSigned(transaction: SignedTransaction)
        fun onStopResources()
        fun onLedgerLoading(ledgerName: String?)
        fun onLedgerLoadingFinished()
    }
}
