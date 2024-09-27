package com.algorand.android.module.currency.data.storage

import android.content.SharedPreferences
import com.algorand.android.module.caching.SharedPrefLocalSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CurrencyLocalSource @Inject constructor(
    sharedPref: SharedPreferences
) : SharedPrefLocalSource<String>(sharedPref) {

    override val key: String
        get() = CURRENCY_PREFERENCE_KEY

    override fun getData(defaultValue: String): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    override fun saveData(data: String) {
        saveData { it.putString(key, data) }
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(key, null)
    }

    companion object {
        private const val CURRENCY_PREFERENCE_KEY = "currency_preference_key"
    }
}
