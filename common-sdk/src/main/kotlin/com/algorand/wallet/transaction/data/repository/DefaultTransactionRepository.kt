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

package com.algorand.wallet.transaction.data.repository

import com.algorand.algosdk.v2.client.Utils
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.transaction.data.model.TrackTransactionRequest
import com.algorand.wallet.transaction.data.service.TransactionsAlgodApiService
import com.algorand.wallet.transaction.data.service.TransactionsMobileApiService
import com.algorand.wallet.transaction.domain.model.TransactionId
import com.algorand.wallet.transaction.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal class DefaultTransactionRepository @Inject constructor(
    private val transactionsAlgodApiService: TransactionsAlgodApiService,
    private val transactionsMobileApiService: TransactionsMobileApiService,
    private val algodClient: AlgodClient?
) : TransactionRepository {

    override suspend fun sendSignedTransaction(signedTransaction: ByteArray): PeraResult<TransactionId> {
        return try {
            val rawTransactionData = signedTransaction.toRequestBody("application/x-binary".toMediaTypeOrNull())
            val response = transactionsAlgodApiService.sendSignedTransaction(rawTransactionData)
            PeraResult.Success(TransactionId(response.txnId.orEmpty()))
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun waitForConfirmation(txnId: TransactionId, maxRoundToWait: Int): PeraResult<TransactionId> {
        return try {
            if (algodClient == null) return PeraResult.Error(Exception())
            withContext(Dispatchers.IO) {
                Utils.waitForConfirmation(algodClient, txnId.value, maxRoundToWait)
            }
            PeraResult.Success(txnId)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun trackTransaction(txnId: TransactionId) {
        try {
            transactionsMobileApiService.trackTransaction(TrackTransactionRequest(txnId.value))
        } catch (e: Exception) {
            // Fire and forget
        }
    }
}
