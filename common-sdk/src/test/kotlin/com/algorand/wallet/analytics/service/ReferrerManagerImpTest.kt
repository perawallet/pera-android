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

package com.algorand.wallet.analytics.data.service

import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.service.PeraReferrerInstallClient
import com.algorand.wallet.analytics.domain.service.PeraReferrerQueryParamParser
import com.algorand.wallet.analytics.domain.usecase.SaveReferrerData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

class PeraReferrerManagerImplTest {

    private lateinit var sut: PeraReferrerManagerImpl

    private val mockReferrerClient: PeraReferrerInstallClient = mockk(relaxed = true)
    private val mockSaveReferrerData: SaveReferrerData = mockk(relaxed = true)
    private val mockQueryParamParser: PeraReferrerQueryParamParser = mockk(relaxed = true)

    private val testReferrerUrl = "https://example.com/app?utm_source=test&utm_medium=email"
    private val testReferrerData = ReferrerData(
        utmSource = "test",
        utmMedium = "email",
        utmCampaign = null,
        utmTerm = null,
        utmContent = null
    )

    @Before
    fun setup() {
        sut = PeraReferrerManagerImpl(
            referrerClient = mockReferrerClient,
            saveReferrerData = mockSaveReferrerData,
            peraReferrerQueryParamParser = mockQueryParamParser
        )

        every { mockQueryParamParser.getReferrerData(any()) } returns testReferrerData
    }

    @Test
    fun `EXPECT URL retrieved and data saved WHEN fetchInstallReferrer is called with non-null URL`() = runTest {
        coEvery { mockReferrerClient.getReferrerUrl() } returns testReferrerUrl

        sut.fetchInstallReferrer()

        coVerify { mockReferrerClient.getReferrerUrl() }
        verify { mockQueryParamParser.getReferrerData(testReferrerUrl) }
        coVerify { mockSaveReferrerData.invoke(testReferrerData) }
    }

    @Test
    fun `EXPECT no data parsing or saving WHEN fetchInstallReferrer is called with null URL`() = runTest {
        coEvery { mockReferrerClient.getReferrerUrl() } returns null

        sut.fetchInstallReferrer()

        coVerify { mockReferrerClient.getReferrerUrl() }
        coVerify(exactly = 0) { mockSaveReferrerData.invoke(any<ReferrerData>()) }
        verify(exactly = 0) { mockQueryParamParser.getReferrerData(any()) }
    }

    @Test
    fun `EXPECT URL parsed and data saved WHEN saveReferrerData is called directly`() = runTest {
        sut.saveReferrerData(testReferrerUrl)

        verify { mockQueryParamParser.getReferrerData(testReferrerUrl) }
        coVerify { mockSaveReferrerData.invoke(testReferrerData) }
    }
}
