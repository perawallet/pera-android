package com.algorand.android.module.transaction.history.component.domain.repository

import com.algorand.android.module.transaction.history.component.domain.model.PaginatedTransactions

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
