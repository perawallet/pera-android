package com.algorand.android.transaction_history_component.domain.mapper

import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.TransactionHistory

internal interface PayTransactionMapper {
    operator fun invoke(transaction: TransactionHistory, address: String): BaseTransactionHistory
}
