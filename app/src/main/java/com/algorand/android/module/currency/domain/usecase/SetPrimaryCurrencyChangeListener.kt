package com.algorand.android.module.currency.domain.usecase

import com.algorand.android.caching.SharedPrefLocalSource

fun interface SetPrimaryCurrencyChangeListener {
    operator fun invoke(listener: SharedPrefLocalSource.OnChangeListener<String>)
}
