package com.algorand.android.parity.domain.usecase.primary

interface GetPrimaryCurrencySymbol {
    operator fun invoke(): String?
}
