package com.algorand.android.parity.domain.usecase

import java.math.BigDecimal

interface GetUsdToAlgoConversionRate {
    operator fun invoke(): BigDecimal
}
