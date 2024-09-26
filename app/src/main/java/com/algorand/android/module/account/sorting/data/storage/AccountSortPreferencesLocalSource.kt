package com.algorand.android.module.account.sorting.data.storage

import android.content.SharedPreferences
import com.algorand.android.caching.SharedPrefLocalSource
import javax.inject.Inject

internal class AccountSortPreferencesLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String>(sharedPreferences) {

    override val key: String
        get() = ACCOUNT_SORT_PREFERENCE_KEY

    override fun getData(defaultValue: String): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(key, null)
    }

    override fun saveData(data: String) {
        saveData { it.putString(key, data) }
    }

    companion object {
        private const val ACCOUNT_SORT_PREFERENCE_KEY = "account_sort_preference"
    }
}
