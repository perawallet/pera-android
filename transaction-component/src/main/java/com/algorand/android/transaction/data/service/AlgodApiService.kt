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

package com.algorand.android.transaction.data.service

import com.algorand.android.transaction.data.model.SendTransactionResponse
import com.algorand.android.transaction.data.model.TransactionParamsResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface AlgodApiService {

    @GET("v2/transactions/params")
    suspend fun getTransactionParams(): Response<TransactionParamsResponse>

    @Headers("Content-Type: application/x-binary")
    @POST("v2/transactions")
    suspend fun sendSignedTransactions(@Body rawTransactionData: RequestBody): Response<SendTransactionResponse>
}
