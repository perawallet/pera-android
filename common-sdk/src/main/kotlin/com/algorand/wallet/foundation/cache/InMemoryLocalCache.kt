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

package com.algorand.wallet.foundation.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class InMemoryLocalCache<KEY, VALUE> : LocalCache<KEY, VALUE> {

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
