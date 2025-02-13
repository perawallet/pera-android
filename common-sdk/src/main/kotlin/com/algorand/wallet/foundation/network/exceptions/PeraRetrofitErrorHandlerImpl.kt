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

package com.algorand.wallet.foundation.network.exceptions

import com.algorand.wallet.foundation.json.JsonSerializer
import com.google.gson.JsonElement
import retrofit2.Response

internal class PeraRetrofitErrorHandlerImpl(
    private val jsonSerializer: JsonSerializer,
    private val defaultErrorMessage: String = "An Error Occurred.",
    private val responseCodesToLog: IntArray = intArrayOf()
) : PeraRetrofitErrorHandler {

    override fun <T> parse(response: Response<T>): ParsedError {
        if (responseCodesToLog.contains(response.code())) {
            sendExceptionLog(UnexpectedResponseCodeException(getLogMessage(response)))
        }

        return convertJsonToParsedError(response)
    }

    /*
        --->
        501 GET http://localhost:56979/
        HEADERS {
            Accept-Encoding gzip
            Connection Keep-Alive
            Host localhost:56979
            User-Agent okhttp/4.2.1
        }
        <--
     */
    private fun <T> getLogMessage(response: Response<T>): String {
        val logStringBuilder = StringBuilder()

        val networkResponseRequest = response.raw().networkResponse?.request

        val responseCode = response.raw().code
        val requestMethod = networkResponseRequest?.method
        val requestUrl = networkResponseRequest?.url

        logStringBuilder.appendLine("HipoExceptionsAndroid")
        logStringBuilder.appendLine("--->")
        logStringBuilder.appendLine("$responseCode $requestMethod $requestUrl ")
        logStringBuilder.appendLine("HEADERS { ")
        val headers = networkResponseRequest?.headers
        headers?.names()?.forEach { headerName ->
            logStringBuilder.appendLine("\t$headerName: ${headers[headerName]}")
        }
        logStringBuilder.appendLine("}\n<--")

        return logStringBuilder.toString()
    }

    private fun <T> convertJsonToParsedError(
        response: Response<T>
    ): ParsedError {
        val errorOutputAsJson = response.errorBody()?.string() ?: ""
        val baseErrorModel = jsonSerializer.fromJson(errorOutputAsJson, BaseError::class.java)
        val detailedKeyErrorMap = getKeyErrorMap(baseErrorModel?.detail)
        val summaryMessageFromMap = detailedKeyErrorMap.values.firstOrNull()?.firstOrNull()
        val fallbackMessage = baseErrorModel?.fallbackMessage
        val summaryMessage =
            when {
                summaryMessageFromMap?.isNotBlank() == true -> summaryMessageFromMap
                fallbackMessage?.isNotEmpty() == true -> {
                    sendExceptionLog(FallbackMessageException(getLogMessage(response)))
                    fallbackMessage
                }
                else -> {
                    sendExceptionLog(NoFallbackMessageException(getLogMessage(response)))
                    defaultErrorMessage
                }
            }
        return ParsedError(detailedKeyErrorMap, summaryMessage, response.code())
    }

    private fun getKeyErrorMap(
        jsonElement: JsonElement?,
        previousPath: String = ""
    ): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()

        if (jsonElement == null || !jsonElement.isJsonObject) {
            return result
        }

        val entrySet = jsonElement.asJsonObject.entrySet()

        entrySet.forEach { (currentKey, jsonElement) ->
            val currentPath = "$previousPath/$currentKey"

            when {
                jsonElement.isJsonPrimitive -> result[currentPath] = listOf(jsonElement.asString)

                jsonElement.isJsonArray -> {
                    val errorMessageList = mutableListOf<String>()
                    jsonElement.asJsonArray.forEach { jsonElementItem ->
                        if (jsonElementItem.isJsonPrimitive) {
                            errorMessageList.add(jsonElementItem.asString)
                        }
                    }
                    result[currentPath] = errorMessageList
                }

                jsonElement.isJsonObject -> result.putAll(
                    getKeyErrorMap(jsonElement.asJsonObject, currentPath)
                )
            }
        }

        return result
    }
}