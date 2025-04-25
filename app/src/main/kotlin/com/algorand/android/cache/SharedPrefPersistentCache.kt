/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.cache

import android.content.SharedPreferences
import com.algorand.wallet.foundation.cache.PersistentCache
import com.google.gson.Gson
import java.lang.reflect.Type

class SharedPrefPersistentCache<T : Any>(
    private val type: Type,
    private val key: String,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : PersistentCache<T> {

    override fun put(data: T) {
        sharedPreferences.edit().apply {
            putString(key, gson.toJson(data))
            apply()
        }
    }

    override fun get(): T? {
        val json = sharedPreferences.getString(key, null).orEmpty()
        return gson.fromJson<T>(json, type)
    }

    override fun clear() {
        sharedPreferences.edit().apply {
            remove(key)
            apply()
        }
    }
}
