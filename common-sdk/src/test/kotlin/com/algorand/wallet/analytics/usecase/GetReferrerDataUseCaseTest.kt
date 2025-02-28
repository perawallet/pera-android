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

package com.algorand.wallet.analytics.domain.usecase

import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.usecases.model.ReferrerData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class GetReferrerDataUseCaseTest {

    private lateinit var sut: GetReferrerDataUseCase

    private val mockReferrerRepository: ReferrerRepository = mockk()

    @Before
    fun setup() {
        sut = GetReferrerDataUseCase(mockReferrerRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockReferrerRepository)
    }

    @Test
    fun `invoke should return referrer data from repository`() = runTest {
        val expectedReferrerData = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "organic",
            utmCampaign = "download_app",
            utmTerm = "crypto_wallet",
            utmContent = "homepage_button"
        )

        coEvery { mockReferrerRepository.getReferrerData() } returns expectedReferrerData

        val result = sut.invoke()

        assertEquals(expectedReferrerData, result)
        coVerify(exactly = 1) { mockReferrerRepository.getReferrerData() }
    }

    @Test
    fun `invoke should handle empty referrer data`() = runTest {
        val emptyReferrerData = ReferrerData(
            utmSource = null,
            utmMedium = null,
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        coEvery { mockReferrerRepository.getReferrerData() } returns emptyReferrerData

        val result = sut.invoke()

        assertEquals(emptyReferrerData, result)
        coVerify(exactly = 1) { mockReferrerRepository.getReferrerData() }
    }

    @Test
    fun `invoke should handle partial referrer data`() = runTest {
        val partialReferrerData = ReferrerData(
            utmSource = "twitter",
            utmMedium = "social",
            utmCampaign = null,
            utmTerm = null,
            utmContent = null
        )

        coEvery { mockReferrerRepository.getReferrerData() } returns partialReferrerData

        val result = sut.invoke()

        assertEquals(partialReferrerData, result)
        coVerify(exactly = 1) { mockReferrerRepository.getReferrerData() }
    }
}
