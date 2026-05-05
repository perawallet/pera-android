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

package com.algorand.wallet.spotbanner.data.repository

import com.algorand.wallet.foundation.network.utils.request
import com.algorand.wallet.spotbanner.data.cache.SpotBannerInMemoryCache
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerCacheDataMapper
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerMapper
import com.algorand.wallet.spotbanner.data.service.SpotBannerApiService
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DefaultSpotBannerRepository @Inject constructor(
    private val spotBannerApiService: SpotBannerApiService,
    private val spotBannerMapper: SpotBannerMapper,
    private val spotBannerCacheDataMapper: SpotBannerCacheDataMapper,
    private val spotBannerCache: SpotBannerInMemoryCache
) : SpotBannerRepository {

    override fun getSpotBannerFlow(): Flow<List<SpotBanner>> {
        return spotBannerCache.observe().map { cacheDataList ->
            cacheDataList.map { cacheData -> spotBannerMapper.map(cacheData) }
        }
    }

    override suspend fun cacheBanners(deviceId: String) {
        try {
            val spotBannersResponse = spotBannerApiService.getSpotBanners(deviceId)
            val cacheData = spotBannersResponse.mapNotNull { response -> spotBannerCacheDataMapper.map(response) }
            spotBannerCache.put(cacheData)
        } catch (_: Exception) {
            spotBannerCache.put(emptyList())
        }
    }

    override suspend fun dismissBanner(deviceId: String, bannerId: Long) {
        spotBannerCache.remove(bannerId)
        request { spotBannerApiService.dismissSpotBanner(deviceId, bannerId) }
    }

    override suspend fun clearBannerCache() {
        spotBannerCache.clear()
    }
}
