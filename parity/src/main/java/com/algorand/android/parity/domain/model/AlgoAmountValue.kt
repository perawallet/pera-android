package com.algorand.android.parity.domain.model

import java.math.*

data class AlgoAmountValue(
    val amount: BigInteger,
    val parityValueInSelectedCurrency: ParityValue,
    val parityValueInSecondaryCurrency: ParityValue,
    val usdValue: BigDecimal
)
