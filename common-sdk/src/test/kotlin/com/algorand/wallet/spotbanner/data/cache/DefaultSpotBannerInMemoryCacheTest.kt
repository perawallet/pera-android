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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.foundation.cache.DefaultFlowInMemoryCache
import com.algorand.wallet.foundation.cache.FakeInMemoryCache
import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import com.algorand.wallet.spotbanner.data.model.SpotBannerCacheData
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultSpotBannerInMemoryCacheTest {

    private val inMemoryCache = FakeInMemoryCache<Array<SpotBannerCacheData>>()
    private val persistentCache: FlowInMemoryCache<Array<SpotBannerCacheData>> = DefaultFlowInMemoryCache(
        inMemoryCache,
        emptyArray<SpotBannerCacheData>()
    )

    private val sut = DefaultSpotBannerInMemoryCache(persistentCache)

    @Test
    fun `EXPECT cache to be cleared`(): TestResult = runTest {
        inMemoryCache.put(arrayOf(SPOT_BANNER_CACHE_1))

        sut.clear()

        assertTrue(persistentCache.get().isEmpty())
    }

    @Test
    fun `EXPECT cache to be updated with new banners only`(): TestResult = runTest {
        inMemoryCache.put(arrayOf(SPOT_BANNER_CACHE_1))

        sut.put(listOf(SPOT_BANNER_CACHE_1, SPOT_BANNER_CACHE_2))
        sut.put(listOf(SPOT_BANNER_CACHE_2))

        val expected = arrayOf(SPOT_BANNER_CACHE_1, SPOT_BANNER_CACHE_2)
        assertTrue(expected.contentEquals(persistentCache.get()))
    }

    @Test
    fun `EXPECT banner to be removed from cache`(): TestResult = runTest {
        inMemoryCache.put(arrayOf(SPOT_BANNER_CACHE_1, SPOT_BANNER_CACHE_2))

        sut.remove(SPOT_BANNER_CACHE_1.id)

        val expected = arrayOf(SPOT_BANNER_CACHE_2)
        assertTrue(expected.contentEquals(persistentCache.get()))
    }

    @Test
    fun `EXPECT cache to be observed`(): TestResult = runTest {
        persistentCache.put(arrayOf(SPOT_BANNER_CACHE_1, SPOT_BANNER_CACHE_2))

        val testObserver = sut.observe().test()
        sut.remove(SPOT_BANNER_CACHE_1.id)

        testObserver.assertValueHistory(
            listOf(SPOT_BANNER_CACHE_1, SPOT_BANNER_CACHE_2),
            listOf(SPOT_BANNER_CACHE_2)
        )
    }

    private companion object {
        val SPOT_BANNER_CACHE_1 = peraFixture<SpotBannerCacheData>().copy(id = 1L)
        val SPOT_BANNER_CACHE_2 = peraFixture<SpotBannerCacheData>().copy(id = 2L)
    }
}
