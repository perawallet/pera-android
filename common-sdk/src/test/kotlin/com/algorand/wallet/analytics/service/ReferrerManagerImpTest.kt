/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.analytics.service

import com.algorand.wallet.analytics.data.service.ReferrerManagerImpl
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.service.ReferrerQueryParamParser
import com.algorand.wallet.analytics.domain.usecases.model.ReferrerData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class ReferrerManagerImplTest {

    private lateinit var sut: ReferrerManagerImpl

    private val mockReferrerRepository: ReferrerRepository = mockk(relaxed = true)
    private val mockReferrerQueryParamParser: ReferrerQueryParamParser = mockk(relaxed = true)

    @Before
    fun setup() {
        sut = ReferrerManagerImpl(
            referrerRespository = mockReferrerRepository,
            referrerQueryParamParser = mockReferrerQueryParamParser
        )
    }

    @After
    fun tearDown() {
        confirmVerified(mockReferrerRepository, mockReferrerQueryParamParser)
    }

    @Test
    fun `saveReferrerData should parse referrer url and save to repository`() = runTest {
        val testReferrerUrl = "https://example.com?utm_source=test&utm_medium=email"
        val mockReferrerData = ReferrerData(
            utmSource = "test",
            utmMedium = "email",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        coEvery {
            mockReferrerQueryParamParser.getReferrerData(testReferrerUrl)
        } returns mockReferrerData

        sut.saveReferrerData(testReferrerUrl)

        coVerify(exactly = 1) {
            mockReferrerQueryParamParser.getReferrerData(testReferrerUrl)
        }
        coVerify(exactly = 1) {
            mockReferrerRepository.saveReferrerData(mockReferrerData)
        }
    }

    @Test
    fun `saveReferrerData should handle empty referrer data`() = runTest {
        val testReferrerUrl = "https://example.com"
        val emptyReferrerData = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        coEvery {
            mockReferrerQueryParamParser.getReferrerData(testReferrerUrl)
        } returns emptyReferrerData

        sut.saveReferrerData(testReferrerUrl)

        coVerify(exactly = 1) {
            mockReferrerQueryParamParser.getReferrerData(testReferrerUrl)
        }
        coVerify(exactly = 1) {
            mockReferrerRepository.saveReferrerData(emptyReferrerData)
        }
    }
}
