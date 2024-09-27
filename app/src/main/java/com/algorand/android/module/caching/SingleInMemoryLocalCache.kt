package com.algorand.android.module.caching

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO Write test
class SingleInMemoryLocalCache<T> {

    val cacheFlow: StateFlow<CacheResult<T>?>
        get() = _cacheFlow
    private val _cacheFlow = MutableStateFlow<CacheResult<T>?>(null)

    fun getOrNull(): CacheResult<T>? = cacheFlow.value

    fun remove(): CacheResult<T>? {
        return _cacheFlow.value.also {
            clear()
        }
    }

    fun clear() {
        _cacheFlow.value = null
    }

    fun put(value: CacheResult<T>) {
        _cacheFlow.value = value
    }
}
