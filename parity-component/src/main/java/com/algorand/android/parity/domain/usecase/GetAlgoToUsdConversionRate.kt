package com.algorand.android.parity.domain.usecase

import java.math.BigDecimal

interface GetAlgoToUsdConversionRate {
    operator fun invoke(): BigDecimal
}
