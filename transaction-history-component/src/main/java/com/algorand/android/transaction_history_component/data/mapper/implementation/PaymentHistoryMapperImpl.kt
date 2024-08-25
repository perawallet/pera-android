package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.PaymentHistoryMapper
import com.algorand.android.transaction_history_component.data.model.PaymentHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.PaymentHistory
import javax.inject.Inject

internal class PaymentHistoryMapperImpl @Inject constructor() : PaymentHistoryMapper {

    override fun invoke(response: PaymentHistoryResponse?): PaymentHistory? {
        if (response == null) return null
        return PaymentHistory(
            amount = response.amount,
            receiverAddress = response.receiverAddress,
            closeAmount = response.closeAmount,
            closeToAddress = response.closeToAddress
        )
    }
}
