package com.algorand.android.module.parity.domain.model

import java.math.BigDecimal
import java.math.BigInteger

data class AlgoAmountValue(
    val amount: BigInteger,
    val parityValueInSelectedCurrency: ParityValue,
    val parityValueInSecondaryCurrency: ParityValue,
    val usdValue: BigDecimal
)
