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

package com.algorand.android.foundation.json

import com.google.gson.Gson
import javax.inject.Inject

internal class PeraSerializer @Inject constructor(val gson: Gson) : JsonSerializer {

    override fun toJson(payload: Any?): String {
        return gson.toJson(payload)
    }

    override fun <T> fromJson(json: String, type: Class<T>): T? {
        return try {
            gson.fromJson(json, type)
        } catch (exception: Exception) {
            null
        }
    }

    override fun <T> fromJson(json: String, token: PeraTypeToken<T>): T? {
        return try {
            val typeToken = PeraTypeTokenProvider(token).getTypeToken()
            gson.fromJson(json, typeToken)
        } catch (exception: Exception) {
            null
        }
    }
}
