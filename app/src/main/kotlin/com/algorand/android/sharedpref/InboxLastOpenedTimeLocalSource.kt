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

import com.algorand.wallet.foundation.cache.PersistentCache
import javax.inject.Inject

// ISO-8601 ISO_DATE_TIME
class InboxLastOpenedTimeLocalSource @Inject constructor(
    private val cache: PersistentCache<String>
) {

    fun getData(defaultValue: String?): String? {
        return cache.get() ?: defaultValue
    }

    fun getDataOrNull(): String? {
        return cache.get()
    }

    fun saveData(data: String) {
        cache.put(data)
    }
}
