package com.algorand.android.currency.domain.usecase

fun interface SetPrimaryCurrencyId {
    operator fun invoke(primaryCurrencyId: String)
}
