package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.PaginatedTransactionsResponse
import com.algorand.android.transaction_history_component.domain.model.PaginatedTransactions

internal interface PaginatedTransactionMapper {
    operator fun invoke(response: PaginatedTransactionsResponse): PaginatedTransactions
}
