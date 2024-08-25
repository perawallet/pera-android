package com.algorand.android.transaction_history_component.domain.model

import java.math.BigInteger

data class PaymentHistory(
    val amount: BigInteger,
    val receiverAddress: String?,
    val closeAmount: BigInteger?,
    val closeToAddress: String?
)
