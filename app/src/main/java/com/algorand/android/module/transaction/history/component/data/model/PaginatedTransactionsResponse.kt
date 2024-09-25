package com.algorand.android.module.transaction.history.component.data.model

import com.google.gson.annotations.SerializedName

internal data class PaginatedTransactionsResponse(
    @SerializedName("next-token")
    val nextToken: String?,
    @SerializedName("transactions")
    val transactionList: List<TransactionHistoryResponse> = listOf()
)
