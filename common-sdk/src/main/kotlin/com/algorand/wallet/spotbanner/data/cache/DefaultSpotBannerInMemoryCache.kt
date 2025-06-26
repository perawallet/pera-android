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

package com.algorand.wallet.spotbanner.data.cache

import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import com.algorand.wallet.spotbanner.data.model.SpotBannerCacheData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultSpotBannerInMemoryCache(
    private val flowInMemoryCache: FlowInMemoryCache<Array<SpotBannerCacheData>>
) : SpotBannerInMemoryCache {

    override fun observe(): Flow<List<SpotBannerCacheData>> {
        return flowInMemoryCache.observe().map { it.toList() }
    }

    override suspend fun put(spotBanners: List<SpotBannerCacheData>) {
        val currentBanners = flowInMemoryCache.get()
        val updatedBanners = currentBanners + spotBanners
        val filteredBanners = updatedBanners.distinctBy { it.id }.toTypedArray()
        flowInMemoryCache.put(filteredBanners)
    }

    override suspend fun remove(id: Long) {
        val currentBanners = flowInMemoryCache.get()
        val updatedBanners = currentBanners.filter { it.id != id }
        flowInMemoryCache.put(updatedBanners.toTypedArray())
    }

    override suspend fun clear() {
        flowInMemoryCache.clear()
    }
}
