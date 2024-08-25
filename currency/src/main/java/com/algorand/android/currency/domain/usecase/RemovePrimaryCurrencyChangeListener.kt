package com.algorand.android.currency.domain.usecase

import com.algorand.android.caching.SharedPrefLocalSource

fun interface RemovePrimaryCurrencyChangeListener {
    operator fun invoke(listener: SharedPrefLocalSource.OnChangeListener<String>)
}
