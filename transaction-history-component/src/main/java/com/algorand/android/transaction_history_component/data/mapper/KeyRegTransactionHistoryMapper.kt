package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.KeyRegTransactionHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.KeyRegTransactionHistory

internal interface KeyRegTransactionHistoryMapper {
    operator fun invoke(response: KeyRegTransactionHistoryResponse?): KeyRegTransactionHistory?
}
