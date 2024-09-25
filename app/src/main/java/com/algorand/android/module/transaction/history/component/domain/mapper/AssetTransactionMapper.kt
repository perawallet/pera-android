package com.algorand.android.module.transaction.history.component.domain.mapper

import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.module.transaction.history.component.domain.model.TransactionHistory

internal interface AssetTransactionMapper {
    operator fun invoke(transaction: TransactionHistory, publicKey: String): BaseTransactionHistory?
}
