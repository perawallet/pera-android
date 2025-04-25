package com.algorand.android.exceptions

import android.util.Log
import com.algorand.android.utils.sendErrorLog

class FallbackMessageException(requestDetail: String) : Exception(requestDetail)
class NoFallbackMessageException(requestDetail: String) : Exception(requestDetail)
class UnexpectedResponseCodeException(requestDetail: String) : Exception(requestDetail)

private const val LIBRARY_TAG = "RetrofitErrorHandler"

fun sendExceptionLog(exception: Exception) {
    try {
        sendErrorLog(exception.toString())
    } catch (exception: Exception) {
        Log.w(LIBRARY_TAG, "Crashlytics has been not started yet.")
    }
}
