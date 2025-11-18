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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultDismissedBannerIdsCacheTest {

    private val persistentCache: PersistentCache<Array<Long>> = mockk(relaxed = true)

    private val sut = DefaultDismissedBannerIdsCache(persistentCache)

    @Test
    fun `EXPECT banner id to be cached WHEN set dismissed invoked and cache is null`(): TestResult = runTest {
        every { persistentCache.get() } returns null

        sut.setDismissed(99L)

        verify { persistentCache.put(arrayOf(99L)) }
    }

    @Test
    fun `EXPECT banner id to be added existing cache WHEN set dismissed invoked and cache is not empty`(): TestResult =
        runTest {
            every { persistentCache.get() } returns arrayOf(1L, 2L, 3L)

            sut.setDismissed(99L)

            verify { persistentCache.put(arrayOf(1L, 2L, 3L, 99L)) }
        }

    @Test
    fun `EXPECT cached banner ids WHEN cache is not empty`(): TestResult = runTest {
        every { persistentCache.get() } returns arrayOf(1L, 2L, 3L)

        val result = sut.getDismissedBannerIds()

        val expected = listOf(1L, 2L, 3L)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty list WHEN cache is null`(): TestResult = runTest {
        every { persistentCache.get() } returns null

        val result = sut.getDismissedBannerIds()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT cache to be cleared`(): TestResult = runTest {
        sut.clear()

        coVerify { persistentCache.clear() }
    }
}
