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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.PeraResult.Error
import com.algorand.wallet.foundation.PeraResult.Success
import com.algorand.wallet.swap.domain.model.SignedSwapTransaction
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason.OTHER
import com.algorand.wallet.transaction.domain.model.TransactionId
import com.algorand.wallet.transaction.domain.usecase.SendSignedTransaction
import javax.inject.Inject
import kotlinx.coroutines.delay

internal class SendSwapTransactionsUseCase @Inject constructor(
    private val sendSignedTransaction: SendSignedTransaction,
    private val setSwapStatusInProgress: SetSwapStatusInProgress,
    private val setSwapStatusFailed: SetSwapStatusFailed
) : SendSwapTransactions {

    override suspend fun invoke(swapId: Long, txns: List<SignedSwapTransaction>): PeraResult<List<TransactionId>> {
        val txnIds = mutableListOf<TransactionId>()
        for (txn in txns) {
            when (val result = sendTransaction(txn)) {
                is Success<TransactionId> -> txnIds.add(result.data)
                is Error -> {
                    if (txn.type.isConfirmationRequired) {
                        setSwapStatusFailed(swapId, OTHER)
                        return result
                    }
                }
            }
        }
        setSwapStatusInProgress(swapId, txnIds)
        return Success(txnIds)
    }

    private suspend fun sendTransaction(txn: SignedSwapTransaction): PeraResult<TransactionId> {
        return sendSignedTransaction(txn.txnByteArray, txn.type.isConfirmationRequired).map { txnId ->
            if (txn.type.isConfirmationRequired) {
                txn.type.confirmationDelay?.let { delay(it) }
            }
            txnId
        }
    }
}
