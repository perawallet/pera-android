package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.PaymentHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.PaymentHistory

internal interface PaymentHistoryMapper {
    operator fun invoke(response: PaymentHistoryResponse?): PaymentHistory?
}
