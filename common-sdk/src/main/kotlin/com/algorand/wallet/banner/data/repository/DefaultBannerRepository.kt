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

package com.algorand.wallet.banner.data.repository

import com.algorand.wallet.banner.data.cache.DismissedBannerIdsCache
import com.algorand.wallet.banner.data.mapper.BannerMapper
import com.algorand.wallet.banner.data.model.BannerCache
import com.algorand.wallet.banner.data.service.BannerApiService
import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.banner.domain.repository.BannerRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultBannerRepository(
    private val bannerApiService: BannerApiService,
    private val bannerMapper: BannerMapper,
    private val dismissedBannerIdsCache: DismissedBannerIdsCache,
    private val bannerCache: FlowInMemoryCache<BannerCache>
) : BannerRepository {

    override fun getBannerFlow(): Flow<Banner?> {
        return bannerCache.observe().map { it.banner }
    }

    override suspend fun cacheBanner(banner: Banner) {
        bannerCache.put(BannerCache(banner))
    }

    override suspend fun getBanners(deviceId: String): PeraResult<List<Banner>> {
        return try {
            val bannersResponse = bannerApiService.getDeviceBanners(deviceId).bannerDetailResponseList.orEmpty()
            val banners = bannersResponse.mapNotNull { response -> bannerMapper.map(response) }
            PeraResult.Success(banners)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun dismissBanner(bannerId: Long) {
        dismissedBannerIdsCache.setDismissed(bannerId)
        bannerCache.clear()
    }

    override suspend fun getDismissedBannerIdList(): List<Long> {
        return dismissedBannerIdsCache.getDismissedBannerIds()
    }

    override suspend fun clearBannerCache() {
        bannerCache.clear()
    }

    override suspend fun clearDismissedBannerIds() {
        dismissedBannerIdsCache.clear()
    }
}
