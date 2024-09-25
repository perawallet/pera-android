/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.banner.data.storage

import android.content.SharedPreferences
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.foundation.json.JsonSerializer
import com.algorand.android.foundation.json.PeraTypeToken

internal class DismissedBannerIdsLocalSource(
    sharedPreferences: SharedPreferences,
    private val jsonSerializer: JsonSerializer
) : SharedPrefLocalSource<List<Long>>(sharedPreferences) {

    override val key: String = "banner_id_list"

    override fun getDataOrNull(): List<Long>? {
        return getParsedData()
    }

    override fun saveData(data: List<Long>) {
        val previousList = getDataOrNull().orEmpty()
        val newList = (previousList + data).toSet()
        saveData { it.putString(key, jsonSerializer.toJson(newList)) }
    }

    override fun getData(defaultValue: List<Long>): List<Long> {
        val defaultList = jsonSerializer.toJson(defaultValue)
        return getParsedData(defaultList).orEmpty()
    }

    private fun getParsedData(defaultValue: String? = null): List<Long>? {
        val typeToken = object : PeraTypeToken<List<Long>> {}
        return jsonSerializer.fromJson(sharedPref.getString(key, defaultValue).orEmpty(), typeToken)
    }
}
