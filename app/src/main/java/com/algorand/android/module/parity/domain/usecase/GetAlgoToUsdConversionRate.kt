package com.algorand.android.module.parity.domain.usecase

import java.math.BigDecimal

interface GetAlgoToUsdConversionRate {
    operator fun invoke(): BigDecimal
}
