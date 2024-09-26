package com.algorand.android.module.parity.domain.model

import java.math.*

data class AlgoAmountValue(
    val amount: BigInteger,
    val parityValueInSelectedCurrency: ParityValue,
    val parityValueInSecondaryCurrency: ParityValue,
    val usdValue: BigDecimal
)
