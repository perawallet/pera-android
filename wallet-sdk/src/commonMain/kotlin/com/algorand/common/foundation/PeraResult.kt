/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.common.foundation

sealed class PeraResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : PeraResult<T>()
    data class Error(val exception: Exception, val code: Int? = null) : PeraResult<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isFailed: Boolean
        get() = this is Error

    fun getDataOrNull(): T? {
        return when (this) {
            is Success -> data
            is Error -> null
        }
    }

    fun getExceptionOrNull(): Exception? {
        return when (this) {
            is Success -> null
            is Error -> exception
        }
    }

    suspend fun <R> use(onSuccess: (suspend (T) -> R), onFailed: (suspend (Exception, Int?) -> R)): R {
        return when (this) {
            is Success -> onSuccess.invoke(data)
            is Error -> onFailed.invoke(exception, code)
        }
    }

    suspend fun <R : Any> map(transform: suspend (T) -> R): PeraResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, code)
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
