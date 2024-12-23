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

package com.algorand.common.foundation.cache

sealed class CacheResult<T> {

    data class Success<T>(
        val data: T,
        val creationTimestamp: Long
    ) : CacheResult<T>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other != null && this::class != other::class) return false

            other as Success<*>

            if (data != other.data) return false
            return creationTimestamp == other.creationTimestamp
        }

        override fun hashCode(): Int {
            return data.hashCode() + creationTimestamp.hashCode()
        }
    }

    class Error<T> private constructor(
        val exception: Throwable,
        val previouslyCachedData: T? = null,
        val previouslyCachedDataCreationTimestamp: Long? = null,
        val code: Int? = null
    ) : CacheResult<T>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other != null && this::class != other::class) return false

            other as Error<*>

            if (exception == other.exception) return false
            if (previouslyCachedData != other.previouslyCachedData) return false
            return previouslyCachedDataCreationTimestamp == other.previouslyCachedDataCreationTimestamp
        }

        override fun hashCode(): Int {
            return exception.hashCode() + previouslyCachedData.hashCode() + previouslyCachedData.hashCode()
        }

        companion object {
            fun <T> create(
                exception: Throwable,
                previousData: Success<T>? = null,
                code: Int? = null
            ): Error<T> {
                return Error(
                    exception,
                    previousData?.data,
                    previousData?.creationTimestamp,
                    code = code
                )
            }
        }
    }

    fun getDataOrNull(): T? = (this as? Success)?.data
}
