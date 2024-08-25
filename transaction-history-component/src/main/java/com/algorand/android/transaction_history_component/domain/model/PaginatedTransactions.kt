package com.algorand.android.transaction_history_component.domain.model

data class PaginatedTransactions(
    val nextToken: String?,
    val transactionList: List<TransactionHistory> = listOf()
)
