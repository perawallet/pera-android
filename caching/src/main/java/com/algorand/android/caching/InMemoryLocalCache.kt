package com.algorand.android.caching

import kotlinx.coroutines.flow.*

class InMemoryLocalCache<KEY, VALUE> : LocalCache<KEY, VALUE> {

    private val cacheFlow = MutableStateFlow(HashMap<KEY, VALUE>())

    private val lockObject: Any = Any()

    override fun getCacheFlow(): Flow<HashMap<KEY, VALUE>> {
        return cacheFlow.asStateFlow()
    }

    override fun putAll(pairs: List<Pair<KEY, VALUE>>) {
        updateFlow { cacheMap ->
            cacheMap.putAll(pairs.map { Pair(it.first, it.second) })
        }
    }

    override fun put(key: KEY, value: VALUE) {
        updateFlow {
            it[key] = value
        }
    }

    override fun get(key: KEY): VALUE? {
        return synchronized(lockObject) {
            cacheFlow.value[key]
        }
    }

    override fun delete(key: KEY) {
        updateFlow {
            it.remove(key)
        }
    }

    override fun clear() {
        updateFlow {
            it.clear()
        }
    }

    override fun getAll(): List<VALUE> {
        return cacheFlow.value.values.toList()
    }

    private fun updateFlow(action: (MutableMap<KEY, VALUE>) -> Unit) {
        synchronized(lockObject) {
            val newMap = cacheFlow.value.toMutableMap()
            action(newMap)
            cacheFlow.value = newMap as HashMap<KEY, VALUE>
        }
    }
}
