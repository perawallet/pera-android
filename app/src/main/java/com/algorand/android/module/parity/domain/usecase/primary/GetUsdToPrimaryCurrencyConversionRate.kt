package com.algorand.android.module.parity.domain.usecase.primary

import java.math.BigDecimal

interface GetUsdToPrimaryCurrencyConversionRate {
    operator fun invoke(): BigDecimal
}
