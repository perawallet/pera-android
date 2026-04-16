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

package com.algorand.wallet.foundation.network.exceptions

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraLogger
import com.google.gson.Gson
import retrofit2.HttpException

private const val TAG = "PeraApiError"

/**
 * Converts an [Exception] into a [PeraResult.Error], parsing the API error body if the exception
 * is an [HttpException]. If the error body contains a user-facing message (from `detail` or
 * `fallback_message`), it wraps the error in a [PeraApiException].
 *
 * Usage in any repository:
 * ```
 * } catch (e: Exception) {
 *     .peraApiError(e, gson)
 * }
 * ```
 */
fun peraApiError(exception: Exception, gson: Gson): PeraResult.Error {
    val httpException = exception as? HttpException
    val code = httpException?.code()
    val errorBody = httpException?.response()?.errorBody()?.string()
    val apiMessage = parseApiErrorMessage(errorBody, gson)
    return if (apiMessage != null) {
        PeraResult.Error(PeraApiException(apiMessage, code, exception), code)
    } else {
        PeraResult.Error(exception, code)
    }
}

private fun parseApiErrorMessage(errorBody: String?, gson: Gson): String? {
    if (errorBody.isNullOrBlank()) return null
    return try {
        val baseError = gson.fromJson(errorBody, BaseError::class.java)
        val detailMessage = baseError?.detail?.let { detail ->
            when {
                detail.isJsonObject -> {
                    val messages = detail.asJsonObject.entrySet().mapNotNull { (_, value) ->
                        when {
                            value.isJsonArray -> value.asJsonArray.firstOrNull()?.asString
                            value.isJsonPrimitive -> value.asString
                            else -> null
                        }
                    }
                    messages.joinToString(". ").ifBlank { null }
                }

                detail.isJsonPrimitive -> detail.asString.ifBlank { null }
                else -> null
            }
        }
        detailMessage ?: baseError?.fallbackMessage
    } catch (e: Exception) {
        PeraLogger.e(TAG, "Failed to parse API error body", e)
        null
    }
}
