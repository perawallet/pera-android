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

package com.algorand.wallet.foundation.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultFlowInMemoryCache<T : Any>(
    private val inMemoryCachedObject: InMemoryCachedObject<T>,
    private val initialValue: T
) : FlowInMemoryCache<T>, InMemoryCachedObject<T> by inMemoryCachedObject {

    private val cacheFlow = MutableStateFlow(get())

    override fun put(value: T) {
        inMemoryCachedObject.put(value)
        cacheFlow.value = value
    }

    override fun clear() {
        inMemoryCachedObject.clear()
        cacheFlow.value = initialValue
    }

    override fun putSilent(value: T) {
        inMemoryCachedObject.put(value)
    }

    override fun observe(): StateFlow<T> {
        return cacheFlow.asStateFlow()
    }

    override fun get(): T {
        return inMemoryCachedObject.get() ?: initialValue
    }
}
