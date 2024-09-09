package com.algorand.android.currency.domain.repository

import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.currency.domain.model.CurrencyOption
import com.algorand.android.foundation.PeraResult

interface CurrencyRepository {
    fun setPrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>)
    fun removePrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>)

    fun getPrimaryCurrencyPreference(): String
    fun setPrimaryCurrencyPreference(currencyPreference: String)

    suspend fun getCurrencyOptionList(): PeraResult<List<CurrencyOption>>
}
