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

package com.algorand.wallet.utils.date.parser

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

internal class ISO8601DateTimeParser @Inject constructor() : DateTimeParser {

    override fun parseOffsetDateTime(dateTime: String): OffsetDateTime? {
        return tryParse(dateTime) ?: tryParseFormatted(dateTime)
    }

    private fun tryParse(dateTime: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(dateTime)
        } catch (e: Exception) {
            null
        }
    }

    private fun tryParseFormatted(dateTime: String): OffsetDateTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
            OffsetDateTime.parse(dateTime, formatter)
        } catch (e: Exception) {
            null
        }
    }
}
