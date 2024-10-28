/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.domain.usecase

import android.content.Context
import com.algorand.android.R
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59Transaction.Arc59OptInTransaction
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59TransactionPayload
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59Transactions
import com.algorand.android.repository.TransactionsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateArc59TransactionsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createArc59OptInTransaction: CreateArc59OptInTransaction,
    private val createArc59SendTransaction: CreateArc59SendTransaction,
    private val transactionsRepository: TransactionsRepository
) : CreateArc59Transactions {

    override suspend fun invoke(
        payload: Arc59TransactionPayload
    ): Flow<Result<Arc59Transactions>> = flow {
        transactionsRepository.getTransactionParams().use(
            onSuccess = {
                emit(it.createArc59Transactions(payload))
            },
            onFailed = { exception, _ ->
                emit(Result.failure(exception))
            }
        )
    }

    private fun TransactionParams.createArc59Transactions(
        payload: Arc59TransactionPayload
    ): Result<Arc59Transactions> {
        val optInTransactions = createOptInTransactions(payload)
        val sendTransactions = createArc59SendTransaction(this, payload)

        if (optInTransactions == null || sendTransactions == null) {
            return Result.failure(Exception(context.getString(R.string.failed_to_create_transactions)))
        }
        return Result.success(Arc59Transactions(optInTransactions, sendTransactions))
    }

    private fun TransactionParams.createOptInTransactions(
        payload: Arc59TransactionPayload
    ): List<Arc59OptInTransaction>? {
        return if (!payload.isArc59OptedIn) {
            createArc59OptInTransaction(this, payload)
        } else {
            emptyList()
        }
    }
}
