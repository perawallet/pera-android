package com.algorand.android.exceptions

import com.google.gson.Gson
import com.google.gson.JsonElement
import retrofit2.Response

// Create class with dagger and use it everywhere and take gson from the dagger also.
class RetrofitErrorHandler(
    private val gson: Gson,
    private val defaultErrorMessage: String = "An Error Occurred.",
    private val responseCodesToLog: IntArray = intArrayOf()
) {

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

        logStringBuilder.appendln("HipoExceptionsAndroid")
        logStringBuilder.appendln("--->")
        logStringBuilder.appendln("$responseCode $requestMethod $requestUrl ")
        logStringBuilder.appendln("HEADERS { ")
        val headers = networkResponseRequest?.headers
        headers?.names()?.forEach { headerName ->
            logStringBuilder.appendln("\t$headerName: ${headers[headerName]}")
        }
        logStringBuilder.appendln("}\n<--")

        return logStringBuilder.toString()
    }

    fun <T> parse(response: Response<T>): ParsedError {
        if (responseCodesToLog.contains(response.code())) {
            sendExceptionLog(UnexpectedResponseCodeException(getLogMessage(response)))
        }

        return convertJsonToParsedError(response)
    }

    private fun <T> convertJsonToParsedError(
        response: Response<T>
    ): ParsedError {
        val errorOutputAsJson = response.errorBody()?.string() ?: ""
        val baseErrorModel = gson.fromJson(errorOutputAsJson, BaseError::class.java)
        val detailedKeyErrorMap = getKeyErrorMap(baseErrorModel.detail)
        val summaryMessageFromMap = detailedKeyErrorMap.values.firstOrNull()?.firstOrNull()
        val fallbackMessage = baseErrorModel.fallbackMessage
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
