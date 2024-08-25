package com.algorand.android.transaction_history_component.domain.mapper

import com.algorand.android.transaction_history_component.domain.model.*
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory

internal interface BaseTransactionHistoryMapper {
    operator fun invoke(
        address: String,
        transactionHistoryList: List<TransactionHistory>
    ): List<BaseTransactionHistory>

    fun mapToTransactionDateTitle(title: String): BaseTransactionHistoryItem
}
