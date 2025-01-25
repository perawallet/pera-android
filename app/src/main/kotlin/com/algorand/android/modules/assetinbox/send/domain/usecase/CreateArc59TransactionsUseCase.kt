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

import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendTransaction
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59TransactionPayload
import com.algorand.android.repository.TransactionsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateArc59TransactionsUseCase @Inject constructor(
    private val createArc59SendTransaction: CreateArc59SendTransaction,
    private val transactionsRepository: TransactionsRepository
) : CreateArc59Transactions {

    override suspend fun invoke(
        payload: Arc59TransactionPayload
    ): Flow<Result<List<Arc59SendTransaction>>> = flow {
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
    ): Result<List<Arc59SendTransaction>> {
        val sendTransactions = createArc59SendTransaction(this, payload) ?: emptyList()
        return Result.success(sendTransactions)
    }
}
