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

package com.algorand.wallet.transaction.history.data.service

import com.algorand.wallet.transaction.history.data.model.TransactionHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

internal interface TransactionHistoryApiService {

    @GET("/v1/accounts/{account_address}/transactions/")
    suspend fun getTransactionHistory(
        @Path("account_address") accountAddress: String,
        @Query("asset_id") assetId: Long? = null,
        @Query("after_time") afterTime: String? = null,
        @Query("before_time") beforeTime: String? = null,
        @Query("limit") limit: Int? = null
    ): TransactionHistoryResponse

    @GET
    suspend fun getTransactionHistoryMore(@Url url: String): TransactionHistoryResponse
}
