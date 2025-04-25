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

package com.algorand.wallet.analytics.repository

import com.algorand.wallet.analytics.data.repository.ReferrerRepositoryImpl
import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.foundation.cache.PersistentCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ReferrerRepositoryImplTest {

    private val sourceCache: PersistentCache<String> = mockk(relaxed = true)
    private val mediumCache: PersistentCache<String> = mockk(relaxed = true)
    private val campaignCache: PersistentCache<String> = mockk(relaxed = true)
    private val termCache: PersistentCache<String> = mockk(relaxed = true)
    private val contentCache: PersistentCache<String> = mockk(relaxed = true)

    private val sut = ReferrerRepositoryImpl(
        utmSourceStorage = sourceCache,
        utmMediumStorage = mediumCache,
        utmCampaignStorage = campaignCache,
        utmTermStorage = termCache,
        utmContentStorage = contentCache
    )

    @Test
    fun `EXPECT data saved to SharedPreferences WHEN saveReferrerData is called with valid data`() = runTest {
        val referrerData = ReferrerData(
            utmSource = "source",
            utmMedium = "medium",
            utmCampaign = "campaign",
            utmTerm = "term",
            utmContent = "content"
        )

        sut.saveReferrerData(referrerData)

        verify { sourceCache.put("source") }
        verify { mediumCache.put("medium") }
        verify { campaignCache.put("campaign") }
        verify { termCache.put("term") }
        verify { contentCache.put("content") }
    }

    @Test
    fun `EXPECT no values saved WHEN saveReferrerData is called with all null values`() = runTest {
        val referrerData = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        sut.saveReferrerData(referrerData)

        verify(exactly = 0) { sourceCache.put(any()) }
        verify(exactly = 0) { mediumCache.put(any()) }
        verify(exactly = 0) { campaignCache.put(any()) }
        verify(exactly = 0) { termCache.put(any()) }
        verify(exactly = 0) { contentCache.put(any()) }
    }

    @Test
    fun `EXPECT complete ReferrerData WHEN getReferrerData is called and SharedPreferences has all values`() = runTest {
        every { sourceCache.get() } returns "source"
        every { mediumCache.get() } returns "medium"
        every { campaignCache.get() } returns "campaign"
        every { termCache.get() } returns "term"
        every { contentCache.get() } returns "content"

        val result = sut.getReferrerData()

        val expected = ReferrerData(
            utmSource = "source",
            utmMedium = "medium",
            utmCampaign = "campaign",
            utmTerm = "term",
            utmContent = "content"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT ReferrerData with null values WHEN getReferrerData is called and SharedPreferences has no values`() =
        runTest {
            every { sourceCache.get() } returns null
            every { mediumCache.get() } returns null
            every { campaignCache.get() } returns null
            every { termCache.get() } returns null
            every { contentCache.get() } returns null

            val result = sut.getReferrerData()

            val expected = ReferrerData(
                utmSource = null,
                utmMedium = null,
                utmCampaign = null,
                utmTerm = null,
                utmContent = null
            )
            assertEquals(expected, result)
        }
}
