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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.foundation.cache.DefaultFlowInMemoryCache
import com.algorand.wallet.foundation.cache.FakeInMemoryCache
import com.algorand.wallet.foundation.cache.FlowInMemoryCache
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultBannerInMemoryCacheTest {

    private val inMemoryCache = FakeInMemoryCache<List<Banner>>()
    private val flowCache: FlowInMemoryCache<List<Banner>> = DefaultFlowInMemoryCache(inMemoryCache, emptyList())

    private val sut = DefaultBannerInMemoryCache(flowCache)

    @Test
    fun `EXPECT banner removed from cache WHEN remove is called`() = runTest {
        inMemoryCache.put(listOf(BANNER_1, BANNER_2))

        sut.remove(BANNER_1.bannerId)

        val expected = listOf(BANNER_2)
        assertEquals(expected, flowCache.get())
    }

    @Test
    fun `EXPECT banner cache to be cleared WHEN clear is called`() = runTest {
        inMemoryCache.put(listOf(BANNER_1, BANNER_2))

        sut.clear()

        assertEquals(emptyList<Banner>(), flowCache.get())
    }

    @Test
    fun `EXPECT banners cached WHEN cacheAll is called`() = runTest {
        sut.cacheAll(listOf(BANNER_1, BANNER_2))

        assertEquals(listOf(BANNER_1, BANNER_2), flowCache.get())
    }

    @Test
    fun `EXPECT observed flow to be updated WHEN cache updated`() = runTest {
        val testObserver = sut.observe().test()

        sut.cacheAll(listOf(BANNER_1))
        sut.cacheAll(listOf(BANNER_2))
        sut.remove(BANNER_1.bannerId)
        sut.clear()

        testObserver.assertValueHistory(
            emptyList(),
            listOf(BANNER_1),
            listOf(BANNER_1, BANNER_2),
            listOf(BANNER_2),
            emptyList()
        )
    }

    private companion object {
        val BANNER_1 = peraFixture<Banner>().copy(bannerId = 1L)
        val BANNER_2 = peraFixture<Banner>().copy(bannerId = 2L)
    }
}
