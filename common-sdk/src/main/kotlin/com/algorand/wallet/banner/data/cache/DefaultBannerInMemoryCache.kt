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

package com.algorand.wallet.banner.data.cache

import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DefaultBannerInMemoryCache @Inject constructor(
    private val flowInMemoryCache: FlowInMemoryCache<List<Banner>>
) : BannerInMemoryCache {

    override fun remove(bannerId: Long) {
        val currentBanners = flowInMemoryCache.get()
        val updatedBanners = currentBanners.filter { it.bannerId != bannerId }
        flowInMemoryCache.put(updatedBanners)
    }

    override fun cacheAll(banners: List<Banner>) {
        val currentBanners = flowInMemoryCache.get()
        val updatedBanners = currentBanners + banners
        flowInMemoryCache.put(updatedBanners)
    }

    override fun observe(): Flow<List<Banner>> = flowInMemoryCache.observe()

    override fun clear() {
        flowInMemoryCache.clear()
    }
}
