/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.autolockmanager.data.local

import android.content.SharedPreferences
import com.algorand.android.sharedpref.SharedPrefLocalSource
import javax.inject.Inject

class AppAtBackgroundPreferencesLocalSource @Inject constructor(
    sharedPreferences: SharedPreferences
) : SharedPrefLocalSource<Long>(sharedPreferences) {
    override val key: String
        get() = APP_AT_BACKGROUND_KEY

    override fun getData(defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    override fun getDataOrNull(): Long? {
        return with(sharedPref) {
            if (contains(APP_AT_BACKGROUND_KEY)) {
                getLong(APP_AT_BACKGROUND_KEY, -1L)
            } else {
                null
            }
        }
    }

    override fun saveData(data: Long) {
        saveData { it.putLong(key, data) }
    }

    companion object {
        private const val APP_AT_BACKGROUND_KEY = "appAtBackground"
    }
}
