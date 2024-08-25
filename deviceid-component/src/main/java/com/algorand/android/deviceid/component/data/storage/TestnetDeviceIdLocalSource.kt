package com.algorand.android.deviceid.component.data.storage

import android.content.SharedPreferences
import com.algorand.android.caching.SharedPrefLocalSource
import javax.inject.Inject

internal class TestnetDeviceIdLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String?>(sharedPreferences) {

    override val key: String = TESTNET_DEVICE_ID_KEY

    override fun getData(defaultValue: String?): String? {
        return sharedPref.getString(TESTNET_DEVICE_ID_KEY, defaultValue)
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(TESTNET_DEVICE_ID_KEY, null)
    }

    override fun saveData(data: String?) {
        saveData { it.putString(TESTNET_DEVICE_ID_KEY, data) }
    }

    companion object {
        private const val TESTNET_DEVICE_ID_KEY = "testnet_device_id"
    }
}
