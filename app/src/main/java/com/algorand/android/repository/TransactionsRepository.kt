/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.repository

import com.algorand.android.models.Result
import com.algorand.android.models.SendTransactionResponse
import com.algorand.android.models.TrackTransactionRequest
import com.algorand.android.network.AlgodApi
import com.algorand.android.network.MobileAlgorandApi
import com.algorand.android.network.getMessageAsResultError
import com.algorand.android.network.request
import com.algorand.android.network.safeApiCall
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class TransactionsRepository @Inject constructor(
    private val mobileAlgorandApi: MobileAlgorandApi,
    private val algodApi: AlgodApi,
    private val hipoApiErrorHandler: RetrofitErrorHandler
) {

    suspend fun sendSignedTransaction(transactionData: ByteArray): Result<SendTransactionResponse> =
        safeApiCall { postSignedTransaction(transactionData) }

    private suspend fun postSignedTransaction(transactionData: ByteArray): Result<SendTransactionResponse> {
        val rawTransactionData = transactionData.toRequestBody("application/x-binary".toMediaTypeOrNull())
        with(algodApi.sendSignedTransaction(rawTransactionData)) {
            return if (isSuccessful && body() != null) {
                Result.Success(body() as SendTransactionResponse)
            } else {
                Result.Error(Exception(errorBody()?.charStream()?.readText()))
            }
        }
    }

    suspend fun postTrackTransaction(trackTransactionRequest: TrackTransactionRequest): Result<Unit> =
        safeApiCall { requestPostTrackTransaction(trackTransactionRequest) }

    private suspend fun requestPostTrackTransaction(trackTransactionRequest: TrackTransactionRequest) = request(
        doRequest = {
            mobileAlgorandApi.trackTransaction(trackTransactionRequest)
        },
        onFailed = { errorResponse ->
            hipoApiErrorHandler.getMessageAsResultError(errorResponse)
        }
    )

    companion object {
        const val DEFAULT_TRANSACTION_COUNT = 15
    }
}
