package com.algorand.android.module.transaction.history.component.domain.mapper

import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.module.transaction.history.component.domain.model.TransactionHistory

internal interface BaseTransactionHistoryMapper {
    operator fun invoke(
        address: String,
        transactionHistoryList: List<TransactionHistory>
    ): List<BaseTransactionHistory>

    fun mapToTransactionDateTitle(title: String): BaseTransactionHistoryItem
}
