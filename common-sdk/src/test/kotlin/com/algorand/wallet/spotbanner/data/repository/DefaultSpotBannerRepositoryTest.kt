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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.spotbanner.data.cache.SpotBannerInMemoryCache
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerCacheDataMapper
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerMapper
import com.algorand.wallet.spotbanner.data.model.SpotBannerCacheData
import com.algorand.wallet.spotbanner.data.model.SpotBannerResponse
import com.algorand.wallet.spotbanner.data.service.SpotBannerApiService
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultSpotBannerRepositoryTest {

    private val spotBannerApiService: SpotBannerApiService = mockk(relaxed = true)
    private val spotBannerMapper: SpotBannerMapper = mockk(relaxed = true)
    private val spotBannerCacheDataMapper: SpotBannerCacheDataMapper = mockk(relaxed = true)
    private val spotBannerCache: SpotBannerInMemoryCache = mockk(relaxed = true)

    private val sut = DefaultSpotBannerRepository(
        spotBannerApiService,
        spotBannerMapper,
        spotBannerCacheDataMapper,
        spotBannerCache
    )

    @Test
    fun `EXPECT cache to be cleared`(): TestResult = runTest {
        sut.clearBannerCache()

        coVerify { spotBannerCache.clear() }
    }

    @Test
    fun `EXPECT banner to be dismissed and removed from cache WHEN dismissBanner is invoked`(): TestResult = runTest {
        sut.dismissBanner(DEVICE_ID, BANNER_1.id)

        coVerify { spotBannerCache.remove(BANNER_1.id) }
        coVerify { spotBannerApiService.dismissSpotBanner(DEVICE_ID, BANNER_1.id) }
    }

    @Test
    fun `EXPECT banners to be cached WHEN there are banners to cache`(): TestResult = runTest {
        coEvery { spotBannerApiService.getSpotBanners(DEVICE_ID) } returns listOf(BANNER_1_RESPONSE, BANNER_2_RESPONSE)
        every { spotBannerCacheDataMapper.map(BANNER_1_RESPONSE) } returns BANNER_1_CACHE_DATA
        every { spotBannerCacheDataMapper.map(BANNER_2_RESPONSE) } returns null

        sut.cacheBanners(DEVICE_ID)

        coVerify { spotBannerCache.put(listOf(BANNER_1_CACHE_DATA)) }
    }

    @Test
    fun `EXPECT empty cache WHEN fetching banners fails`(): TestResult = runTest {
        coEvery { spotBannerApiService.getSpotBanners(DEVICE_ID) } throws Exception()

        sut.cacheBanners(DEVICE_ID)

        coVerify { spotBannerCache.put(emptyList()) }
    }

    @Test
    fun `EXPECT banner flow to be updated WHEN cache is updated`(): TestResult = runTest {
        val cacheFlow = MutableStateFlow(emptyList<SpotBannerCacheData>())
        every { spotBannerCache.observe() } returns cacheFlow
        every { spotBannerMapper.map(BANNER_1_CACHE_DATA) } returns BANNER_1

        val testObserver = sut.getSpotBannerFlow().test()
        cacheFlow.value = listOf(BANNER_1_CACHE_DATA)
        cacheFlow.value = emptyList()

        testObserver.assertValueHistory(
            emptyList(),
            listOf(BANNER_1),
            emptyList()
        )
    }

    private companion object {
        const val DEVICE_ID = "device-id"
        val BANNER_1 = peraFixture<SpotBanner.Generic>().copy(id = 1L)
        val BANNER_1_CACHE_DATA = peraFixture<SpotBannerCacheData>().copy(id = 1L)
        val BANNER_1_RESPONSE = peraFixture<SpotBannerResponse>().copy(id = 1L)
        val BANNER_2_RESPONSE = peraFixture<SpotBannerResponse>().copy(id = 2L)
    }
}
