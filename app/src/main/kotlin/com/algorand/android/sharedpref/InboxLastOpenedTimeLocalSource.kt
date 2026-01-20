/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.sharedpref

import android.content.SharedPreferences
import javax.inject.Inject

// ISO-8601 ISO_DATE_TIME
class InboxLastOpenedTimeLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<String?>(sharedPreferences) {

    override val key: String
        get() = INBOX_LAST_OPENED_TIME_KEY

    override fun getData(defaultValue: String?): String? {
        return sharedPref.getString(key, defaultValue)
    }

    override fun getDataOrNull(): String? {
        return sharedPref.getString(key, defaultInboxLastOpenedTimePreferences)
    }

    override fun saveData(data: String?) {
        saveData { it.putString(key, data) }
    }

    companion object {
        val defaultInboxLastOpenedTimePreferences: String? = null
        private const val INBOX_LAST_OPENED_TIME_KEY = "inbox_last_opened_time_key"
    }
}
