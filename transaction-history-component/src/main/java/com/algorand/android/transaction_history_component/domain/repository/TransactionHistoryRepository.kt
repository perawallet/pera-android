package com.algorand.android.transaction_history_component.domain.repository

import com.algorand.android.transaction_history_component.domain.model.PaginatedTransactions

internal interface TransactionHistoryRepository {

    suspend fun getTransactionHistory(
        assetId: Long? = null,
        publicKey: String,
        fromDate: String? = null,
        toDate: String? = null,
        nextToken: String? = null,
        limit: Int?,
        txnType: String? = null
    ): Result<PaginatedTransactions>
}
