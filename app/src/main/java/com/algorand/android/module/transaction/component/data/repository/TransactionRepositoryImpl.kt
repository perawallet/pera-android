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

package com.algorand.android.module.transaction.component.data.repository

import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.foundation.PeraResult.Error
import com.algorand.android.module.foundation.PeraResult.Success
import com.algorand.android.module.transaction.component.data.mapper.TransactionParamsMapper
import com.algorand.android.module.transaction.component.data.service.AlgodApiService
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.model.TransactionId
import com.algorand.android.module.transaction.component.domain.model.TransactionParams
import com.algorand.android.module.transaction.component.domain.repository.TransactionRepository
import com.algorand.android.module.network.request
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal class TransactionRepositoryImpl @Inject constructor(
    private val algodApiService: AlgodApiService,
    private val transactionParamsMapper: TransactionParamsMapper
) : TransactionRepository {

    override suspend fun getTransactionParams(): PeraResult<TransactionParams> {
        return request { algodApiService.getTransactionParams() }.use(
            onSuccess = { response ->
                transactionParamsMapper(response)?.let { Success(it) } ?: Error(IllegalArgumentException())
            },
            onFailed = { exception, code -> Error(exception, code) }
        )
    }

    override suspend fun sendSignedTransactions(signedTransaction: SignedTransaction): PeraResult<TransactionId> {
        val payload = signedTransaction.value.toRequestBody("application/x-binary".toMediaTypeOrNull())
        return request { algodApiService.sendSignedTransactions(payload) }.map {
            TransactionId(it.txId.orEmpty())
        }
    }
}
