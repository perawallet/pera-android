/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.banner.data.repository

import com.algorand.android.banner.data.mapper.BannerMapper
import com.algorand.android.banner.data.model.BannerListResponse
import com.algorand.android.banner.data.service.BannerApi
import com.algorand.android.banner.domain.model.Banner
import com.algorand.android.banner.domain.repository.BannerRepository
import com.algorand.android.caching.CacheResult
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.network_utils.request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class BannerRepositoryImpl(
    private val bannerApi: BannerApi,
    private val dismissedBannerIdsLocalSource: SharedPrefLocalSource<List<Long>>,
    private val bannerCache: SingleInMemoryLocalCache<Banner>,
    private val bannerMapper: BannerMapper
) : BannerRepository {

    override suspend fun initializeBanners(deviceId: String) {
        clearBanners()
        val bannerListResponse = request { bannerApi.getBanners(deviceId) }.getDataOrNull()
        val bannerToCache = getBannerToCache(bannerListResponse)
        bannerToCache?.let { bannerCache.put(CacheResult.Success.create(it)) }
    }

    override suspend fun getBannerFlow(): Flow<Banner?> {
        return bannerCache.cacheFlow.map {
            it?.getDataOrNull()
        }
    }

    override suspend fun dismissBanner(bannerId: Long) {
        clearBanners()
        dismissedBannerIdsLocalSource.saveData(listOf(bannerId))
    }

    override suspend fun clearBanners() {
        bannerCache.clear()
    }

    override suspend fun clearDismissedBannerIds() {
        dismissedBannerIdsLocalSource.clear()
    }

    private fun getBannerToCache(bannerListResponse: BannerListResponse?): Banner? {
        val dismissedBannerIds = dismissedBannerIdsLocalSource.getData(emptyList())
        val bannerToCache = bannerListResponse?.bannerDetailResponseList?.firstOrNull { bannerDetail ->
            !dismissedBannerIds.contains(bannerDetail.bannerId ?: return@firstOrNull false)
        }
        return bannerToCache?.let { bannerMapper(it) }
    }
}
