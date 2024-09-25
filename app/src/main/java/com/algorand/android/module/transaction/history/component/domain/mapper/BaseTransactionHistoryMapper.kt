package com.algorand.android.module.transaction.history.component.domain.mapper

import com.algorand.android.module.transaction.history.component.domain.model.*
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory

internal interface BaseTransactionHistoryMapper {
    operator fun invoke(
        address: String,
        transactionHistoryList: List<TransactionHistory>
    ): List<BaseTransactionHistory>

    fun mapToTransactionDateTitle(title: String): BaseTransactionHistoryItem
}
