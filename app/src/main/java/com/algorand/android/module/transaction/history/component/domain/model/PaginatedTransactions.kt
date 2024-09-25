package com.algorand.android.module.transaction.history.component.domain.model

data class PaginatedTransactions(
    val nextToken: String?,
    val transactionList: List<TransactionHistory> = listOf()
)
