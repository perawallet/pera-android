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

package com.algorand.android.network_utils

import com.algorand.android.foundation.PeraResult
import com.hipo.hipoexceptionsandroid.RetrofitErrorHandler
import java.io.IOException
import retrofit2.Response

/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Result.Error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> PeraResult<T>): PeraResult<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        PeraResult.Error(IOException(null, e))
    }
}

// TODO Remove this one and use new function
suspend fun <T : Any> requestOld(
    onFailed: ((Response<T>) -> Result<T>)? = null,
    doRequest: suspend () -> Response<T>
): Result<T> {
    return try {
        with(doRequest()) {
            if (isSuccessful && body() != null) {
                Result.success(body() as T)
            } else {
                onFailed?.invoke(this) ?: Result.failure(Exception(errorBody().toString()))
            }
        }
    } catch (e: Exception) {
        Result.failure(IOException(null, e))
    }
}

suspend fun <T : Any> request(
    onFailed: ((Response<T>) -> PeraResult<T>)? = null,
    doRequest: suspend () -> Response<T>
): PeraResult<T> {
    return safeApiCall {
        with(doRequest()) {
            if (isSuccessful && body() != null) {
                PeraResult.Success(body() as T)
            } else {
                onFailed?.invoke(this) ?: PeraResult.Error(Exception(errorBody().toString()), code())
            }
        }
    }
}

suspend fun <T : Any> requestWithHipoErrorHandler(
    hipoApiErrorHandler: RetrofitErrorHandler,
    doRequest: suspend () -> Response<T>
): PeraResult<T> {
    return request(
        doRequest = doRequest,
        onFailed = { errorResponse -> hipoApiErrorHandler.getMessageAsResultError(errorResponse) }
    )
}

fun <T : Any> RetrofitErrorHandler.getMessageAsResultError(response: Response<T>): PeraResult<T> {
    return PeraResult.Error(Exception(parse(response).message))
}
