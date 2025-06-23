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

import com.algorand.wallet.foundation.cache.PersistentCache

internal class DefaultDismissedBannerIdsCache(
    private val persistentCache: PersistentCache<Array<Long>>
) : DismissedBannerIdsCache {

    override suspend fun setDismissed(bannerId: Long) {
        val currentDismissedBannerIds = persistentCache.get() ?: emptyArray()
        val updatedDismissedBannerIds = currentDismissedBannerIds + bannerId
        persistentCache.put(updatedDismissedBannerIds)
    }

    override suspend fun getDismissedBannerIds(): List<Long> {
        return persistentCache.get()?.toList() ?: emptyList()
    }

    override suspend fun clear() {
        persistentCache.clear()
    }
}
