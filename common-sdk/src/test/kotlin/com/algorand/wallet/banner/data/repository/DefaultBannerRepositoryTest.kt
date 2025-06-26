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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.banner.data.cache.DismissedBannerIdsCache
import com.algorand.wallet.banner.data.mapper.BannerMapper
import com.algorand.wallet.banner.data.model.BannerCache
import com.algorand.wallet.banner.data.model.BannerDetailResponse
import com.algorand.wallet.banner.data.model.BannerListResponse
import com.algorand.wallet.banner.data.service.BannerApiService
import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.DefaultFlowInMemoryCache
import com.algorand.wallet.foundation.cache.FakeInMemoryCache
import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultBannerRepositoryTest {

    private val bannerApiService: BannerApiService = mockk(relaxed = true)
    private val bannerMapper: BannerMapper = mockk(relaxed = true)
    private val dismissedBannerIdsCache: DismissedBannerIdsCache = mockk(relaxed = true)
    private val fakeInMemoryCache = FakeInMemoryCache<BannerCache>()
    private val bannerCache: FlowInMemoryCache<BannerCache> = DefaultFlowInMemoryCache(
        fakeInMemoryCache,
        BannerCache(null)
    )

    private val sut = DefaultBannerRepository(
        bannerApiService,
        bannerMapper,
        dismissedBannerIdsCache,
        bannerCache
    )

    @Test
    fun `EXPECT banner cache flow WHEN getBannerFlow is invoked`() = runTest {
        bannerCache.put(BannerCache(BANNER_1))

        val testObserver = sut.getBannerFlow().test()

        testObserver.assertValueHistory(BANNER_1)
    }

    @Test
    fun `EXPECT banner cache cleared WHEN clearBannerCache is invoked`() = runTest {
        fakeInMemoryCache.put(BannerCache(BANNER_1))

        sut.clearBannerCache()

        assertNull(fakeInMemoryCache.get())
    }

    @Test
    fun `EXPECT banner list WHEN get device banners call is successful`() = runTest {
        coEvery { bannerApiService.getDeviceBanners(DEVICE_ID) } returns BANNER_RESPONSE
        every { bannerMapper.map(BANNER_1_RESPONSE) } returns BANNER_1
        every { bannerMapper.map(BANNER_2_RESPONSE) } returns BANNER_2

        val result = sut.getBanners(DEVICE_ID)

        val expected = PeraResult.Success(listOf(BANNER_1, BANNER_2))
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty list WHEN banner detail response is null`() = runTest {
        coEvery { bannerApiService.getDeviceBanners(DEVICE_ID) } returns BannerListResponse(0, null)

        val result = sut.getBanners(DEVICE_ID)

        val expected = PeraResult.Success<List<Banner>>(emptyList())
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT error WHEN get device banners call fails`() = runTest {
        coEvery { bannerApiService.getDeviceBanners(DEVICE_ID) } throws Exception("Network error")

        val result = sut.getBanners(DEVICE_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT banner to be removed from cache and id to be stored WHEN dismissBanner is invoked`() = runTest {
        fakeInMemoryCache.put(BannerCache(BANNER_1))

        sut.dismissBanner(BANNER_1.bannerId)

        coVerify { dismissedBannerIdsCache.setDismissed(BANNER_1.bannerId) }
        assertNull(fakeInMemoryCache.get())
    }

    @Test
    fun `EXPECT dismissed banner ids to be cleared WHEN clearDismissedBannerIds is invoked`() = runTest {
        sut.clearDismissedBannerIds()

        coVerify { dismissedBannerIdsCache.clear() }
    }

    @Test
    fun `EXPECT dismissed banned ids WHEN getDismissedBannerIdList is invoked`() = runTest {
        coEvery { dismissedBannerIdsCache.getDismissedBannerIds() } returns listOf(1L, 2L, 3L)

        val result = sut.getDismissedBannerIdList()

        val expected = listOf(1L, 2L, 3L)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT banners to be cached WHEN cacheBanners is invoked`() = runTest {
        sut.cacheBanner(BANNER_1)

        assertEquals(BannerCache(BANNER_1), fakeInMemoryCache.get())
    }

    private companion object {
        const val DEVICE_ID = "device-id"
        val BANNER_1 = peraFixture<Banner>().copy(bannerId = 1L)
        val BANNER_2 = peraFixture<Banner>().copy(bannerId = 2L)

        val BANNER_1_RESPONSE = peraFixture<BannerDetailResponse>().copy(bannerId = 1L)
        val BANNER_2_RESPONSE = peraFixture<BannerDetailResponse>().copy(bannerId = 2L)
        val BANNER_RESPONSE = BannerListResponse(2, listOf(BANNER_1_RESPONSE, BANNER_2_RESPONSE))
    }
}
