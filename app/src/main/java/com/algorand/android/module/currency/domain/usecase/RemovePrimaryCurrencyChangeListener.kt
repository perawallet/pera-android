package com.algorand.android.module.currency.domain.usecase

import com.algorand.android.caching.SharedPrefLocalSource

fun interface RemovePrimaryCurrencyChangeListener {
    operator fun invoke(listener: SharedPrefLocalSource.OnChangeListener<String>)
}
