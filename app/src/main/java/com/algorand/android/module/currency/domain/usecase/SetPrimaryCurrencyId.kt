package com.algorand.android.module.currency.domain.usecase

fun interface SetPrimaryCurrencyId {
    operator fun invoke(primaryCurrencyId: String)
}
