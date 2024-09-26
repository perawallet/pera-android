package com.algorand.android.module.deviceid.data.storage

import android.content.SharedPreferences
import com.algorand.android.module.caching.SharedPrefLocalSource
import javax.inject.Inject

/**
 * This class is being used to support previous versions (Before 5.2.2)
 * There was only one device id but right now, there are 2 device ids for different nodes (Mainnet, Testnet)
 * This class represent previously held user id.
 */
internal class NotificationUserIdLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String?>(sharedPreferences) {

    override val key: String = NOTIFICATION_USER_ID_KEY

    override fun getData(defaultValue: String?): String? {
        return sharedPref.getString(NOTIFICATION_USER_ID_KEY, defaultValue)
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(NOTIFICATION_USER_ID_KEY, null)
    }

    override fun saveData(data: String?) {
        saveData { it.putString(NOTIFICATION_USER_ID_KEY, data) }
    }

    companion object {
        private const val NOTIFICATION_USER_ID_KEY = "notification_user_id"
    }
}
