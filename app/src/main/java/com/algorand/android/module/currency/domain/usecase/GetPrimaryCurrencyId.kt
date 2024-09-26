package com.algorand.android.module.currency.domain.usecase

fun interface GetPrimaryCurrencyId {
    operator fun invoke(): String
}
