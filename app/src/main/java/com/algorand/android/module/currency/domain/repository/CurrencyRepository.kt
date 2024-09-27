package com.algorand.android.module.currency.domain.repository

import com.algorand.android.module.caching.SharedPrefLocalSource
import com.algorand.android.module.currency.domain.model.CurrencyOption
import com.algorand.android.module.foundation.PeraResult

interface CurrencyRepository {
    fun setPrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>)
    fun removePrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>)

    fun getPrimaryCurrencyPreference(): String
    fun setPrimaryCurrencyPreference(currencyPreference: String)

    suspend fun getCurrencyOptionList(): PeraResult<List<CurrencyOption>>
}
