package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.PaginatedTransactionsResponse
import com.algorand.android.module.transaction.history.component.domain.model.PaginatedTransactions

internal interface PaginatedTransactionMapper {
    operator fun invoke(response: PaginatedTransactionsResponse): PaginatedTransactions
}
