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

package com.algorand.wallet.inbox.data.cache

import com.algorand.wallet.foundation.cache.InMemoryCacheProvider
import com.algorand.wallet.foundation.cache.InMemoryCachedObject
import com.algorand.wallet.inbox.domain.model.InboxMessages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DefaultInboxInMemoryCache(
    inMemoryCacheProvider: InMemoryCacheProvider
) : InboxInMemoryCache {

    private val cache: InMemoryCachedObject<InboxMessages> = inMemoryCacheProvider.getInMemoryCache()
    private val cacheFlow = MutableStateFlow<InboxMessages?>(null)

    override fun observe(): Flow<InboxMessages?> {
        return cacheFlow.asStateFlow()
    }

    override suspend fun put(inboxMessages: InboxMessages) {
        cache.put(inboxMessages)
        cacheFlow.value = inboxMessages
    }

    override suspend fun clear() {
        cache.clear()
        cacheFlow.value = null
    }

    override suspend fun get(): InboxMessages? {
        return cache.get()
    }
}
