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

package com.algorand.android.credentials.passkeys.foundation

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

internal interface PasskeyJsonSerializer {
    fun <T> decode(json: String, deserializer: DeserializationStrategy<T>): T
    fun encode(value: Any): String
}

internal class PeraPasskeyJsonSerializer(private val json: Json) : PasskeyJsonSerializer {
    override fun <T> decode(json: String, deserializer: DeserializationStrategy<T>): T {
        return this.json.decodeFromString(deserializer, json)
    }

    override fun encode(value: Any): String {
        return json.encodeToString(serializer(value::class.java), value)
    }
}

internal inline fun <reified T> PasskeyJsonSerializer.decode(json: String): T {
    return decode(json, serializer())
}
