package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.TransactionTypeResponse
import com.algorand.android.transaction_history_component.domain.model.TransactionType

internal interface TransactionTypeMapper {
    operator fun invoke(response: TransactionTypeResponse?): TransactionType?
}
