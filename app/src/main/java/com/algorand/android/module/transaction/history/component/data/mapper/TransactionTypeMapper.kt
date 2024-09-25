package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.TransactionTypeResponse
import com.algorand.android.module.transaction.history.component.domain.model.TransactionType

internal interface TransactionTypeMapper {
    operator fun invoke(response: TransactionTypeResponse?): TransactionType?
}
