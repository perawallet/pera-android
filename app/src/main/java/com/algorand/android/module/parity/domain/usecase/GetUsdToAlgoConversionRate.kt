package com.algorand.android.module.parity.domain.usecase

import java.math.BigDecimal

interface GetUsdToAlgoConversionRate {
    operator fun invoke(): BigDecimal
}
