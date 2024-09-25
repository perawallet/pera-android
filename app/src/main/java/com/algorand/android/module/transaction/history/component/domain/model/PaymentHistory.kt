package com.algorand.android.module.transaction.history.component.domain.model

import java.math.BigInteger

data class PaymentHistory(
    val amount: BigInteger,
    val receiverAddress: String?,
    val closeAmount: BigInteger?,
    val closeToAddress: String?
)
