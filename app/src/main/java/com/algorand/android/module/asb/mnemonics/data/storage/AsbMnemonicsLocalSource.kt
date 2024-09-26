package com.algorand.android.module.asb.mnemonics.data.storage

import android.content.SharedPreferences
import com.algorand.android.caching.SharedPrefLocalSource
import javax.inject.Inject

internal class AsbMnemonicsLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String>(sharedPreferences) {

    override val key: String
        get() = ASB_MNEMONICS_KEY

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
        private const val ASB_MNEMONICS_KEY = "asb_mnemonics_key"
    }
}
