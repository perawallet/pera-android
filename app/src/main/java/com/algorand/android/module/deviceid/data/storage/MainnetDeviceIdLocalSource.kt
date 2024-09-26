package com.algorand.android.module.deviceid.data.storage

import android.content.SharedPreferences
import com.algorand.android.module.caching.SharedPrefLocalSource
import javax.inject.Inject

internal class MainnetDeviceIdLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String?>(sharedPreferences) {

    override val key: String = MAINNET_DEVICE_ID_KEY

    override fun getData(defaultValue: String?): String? {
        return sharedPref.getString(MAINNET_DEVICE_ID_KEY, defaultValue)
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(MAINNET_DEVICE_ID_KEY, null)
    }

    override fun saveData(data: String?) {
        saveData { it.putString(MAINNET_DEVICE_ID_KEY, data) }
    }

    companion object {
        private const val MAINNET_DEVICE_ID_KEY = "mainnet_device_id"
    }
}
