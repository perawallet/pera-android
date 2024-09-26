package com.algorand.android.module.caching

sealed class CacheResult<T> {

    data class Success<T>(
        val data: T,
        val creationTimestamp: Long
    ) : CacheResult<T>() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success<*>

            if (data != other.data) return false
            return creationTimestamp == other.creationTimestamp
        }

        override fun hashCode(): Int {
            return data.hashCode() + creationTimestamp.hashCode()
        }

        companion object {
            fun <T> create(data: T): Success<T> {
                return Success(
                    data,
                    createCreationTimestamp()
                )
            }
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
            if (javaClass != other?.javaClass) return false

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

    companion object {
        fun createCreationTimestamp() = System.currentTimeMillis()
    }
}
