package com.algorand.android.currency.domain.usecase

fun interface GetPrimaryCurrencyId {
    operator fun invoke(): String
}
