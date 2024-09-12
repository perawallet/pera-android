package com.algorand.android.network_utils.exceptions

import android.util.Log

class FallbackMessageException(requestDetail: String) : Exception(requestDetail)
class NoFallbackMessageException(requestDetail: String) : Exception(requestDetail)
class UnexpectedResponseCodeException(requestDetail: String) : Exception(requestDetail)

private const val LIBRARY_TAG = "RetrofitErrorHandler"

fun sendExceptionLog(exception: Exception) {
    try {
        // TODO sendErrorLog(exception.toString())
    } catch (exception: Exception) {
        Log.w(LIBRARY_TAG, "Crashlytics has been not started yet.")
    }
}
