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

package com.algorand.common.foundation.network

import com.algorand.common.foundation.PeraResult
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException

suspend inline fun <reified T : Any> safeRequest(
    request: () -> HttpResponse
): PeraResult<T> {
    return try {
        request().toPeraResult<T>()
    } catch (exception: Exception) {
        PeraResult.Error(IOException("API request is failed on the client"), 99)
    }
}

suspend inline fun <reified T : Any> HttpResponse.toPeraResult(): PeraResult<T> {
    return if (this.status.value in 200..299) {
        PeraResult.Success(this.body())
    } else {
        PeraResult.Error(Exception("Error"), this.status.value)
    }
}
