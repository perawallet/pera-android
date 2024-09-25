package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.PaymentHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.PaymentHistory

internal interface PaymentHistoryMapper {
    operator fun invoke(response: PaymentHistoryResponse?): PaymentHistory?
}
