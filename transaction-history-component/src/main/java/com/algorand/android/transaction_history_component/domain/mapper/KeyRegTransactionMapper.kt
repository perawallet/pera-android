package com.algorand.android.transaction_history_component.domain.mapper

import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.transaction_history_component.domain.model.TransactionHistory

internal interface KeyRegTransactionMapper {
    operator fun invoke(transaction: TransactionHistory): BaseTransactionHistory
}
