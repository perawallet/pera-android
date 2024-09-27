package com.algorand.android.module.transaction.history.component.data.service

import com.algorand.android.module.transaction.history.component.data.model.PaginatedTransactionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TransactionHistoryApiService {

    @GET("v2/accounts/{public_key}/transactions")
    suspend fun getTransactionHistory(
        @Path("public_key") publicKey: String,
        @Query("asset-id") assetId: Long?,
        @Query("after-time") afterTime: String?,
        @Query("before-time") beforeTime: String?,
        @Query("next") nextToken: String?,
        @Query("limit") limit: Int?,
        @Query("tx-type") transactionType: String?
    ): Response<PaginatedTransactionsResponse>
}
