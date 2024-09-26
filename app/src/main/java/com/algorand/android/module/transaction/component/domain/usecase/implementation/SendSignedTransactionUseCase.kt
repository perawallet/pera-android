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

package com.algorand.android.module.transaction.component.domain.usecase.implementation

import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.foundation.PeraResult
import com.algorand.android.foundation.PeraResult.Error
import com.algorand.android.foundation.PeraResult.Success
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.model.TransactionId
import com.algorand.android.module.transaction.component.domain.repository.TransactionRepository
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import javax.inject.Inject

internal class SendSignedTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val algoSdkTransaction: AlgoSdkTransaction
) : SendSignedTransaction {

    override suspend fun invoke(
        transaction: SignedTransaction,
        waitForConfirmation: Boolean
    ): PeraResult<TransactionId> {
        return transactionRepository.sendSignedTransactions(transaction).use(
            onSuccess = {
                if (waitForConfirmation) {
                    waitForTransactionConfirmation(it)
                } else {
                    Success(it)
                }
            },
            onFailed = { exception, i -> Error(exception, i) }
        )
    }

    private suspend fun waitForTransactionConfirmation(txId: TransactionId): PeraResult<TransactionId> {
        return algoSdkTransaction.waitForConfirmation(txnId = txId.value, DEFAULT_MAX_ROUND_TO_WAIT).fold(
            onSuccess = { Success(TransactionId(it)) },
            onFailure = { Error(Exception(it.message)) }
        )
    }

    companion object {
        private const val DEFAULT_MAX_ROUND_TO_WAIT = 3
    }
}
