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

package com.algorand.android.module.swap.ui.txnstatus.usecase

import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.foundation.PeraResult.Success
import com.algorand.android.module.swap.ui.txnstatus.model.SignedSwapTransaction
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import javax.inject.Inject

internal class SendSignedSwapTransactionsUseCase @Inject constructor(
    private val sendSignedTransaction: SendSignedTransaction
) : SendSignedSwapTransactions {

    override suspend fun invoke(signedTransactions: List<SignedSwapTransaction>): PeraResult<Unit> {
        val mutableSignedTransactions = signedTransactions.toMutableList()
        val signedSwapQuoteTransaction = mutableSignedTransactions.removeFirstOrNull() ?: return Success(Unit)
        return sendTransaction(signedSwapQuoteTransaction, mutableSignedTransactions)
    }

    private suspend fun sendTransaction(
        signedSwapQuoteTransaction: SignedSwapTransaction,
        remainingTransactionsToSend: MutableList<SignedSwapTransaction>
    ): PeraResult<Unit> {
        val transaction = signedSwapQuoteTransaction.transaction ?: return PeraResult.Error(IllegalArgumentException())
        val signedTransaction = SignedTransaction(transaction)
        return sendSignedTransaction(signedTransaction, signedSwapQuoteTransaction.isTransactionConfirmationNeed).map {
            invoke(remainingTransactionsToSend)
        }
    }
}
