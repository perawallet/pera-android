package com.algorand.android.transaction_history_component.data.service

import com.algorand.android.transaction_history_component.data.model.PaginatedTransactionsResponse
import retrofit2.Response
import retrofit2.http.*

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
