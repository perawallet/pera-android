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
    } catch (_: Exception) {
        Log.w(LIBRARY_TAG, "Crashlytics has been not started yet.")
    }
}
