package com.algorand.android.parity.domain.usecase.secondary

import java.math.BigDecimal

interface GetUsdToSecondaryCurrencyConversionRate {
    operator fun invoke(): BigDecimal
}
