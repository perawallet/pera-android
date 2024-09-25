package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.KeyRegTransactionHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.KeyRegTransactionHistory

internal interface KeyRegTransactionHistoryMapper {
    operator fun invoke(response: KeyRegTransactionHistoryResponse?): KeyRegTransactionHistory?
}
