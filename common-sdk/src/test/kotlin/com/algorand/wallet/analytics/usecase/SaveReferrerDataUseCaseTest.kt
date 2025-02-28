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
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class SaveReferrerDataUseCaseTest {

    private lateinit var sut: SaveReferrerDataUseCase

    private val mockReferrerRepository: ReferrerRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        sut = SaveReferrerDataUseCase(mockReferrerRepository)
    }

    @After
    fun tearDown() {
        confirmVerified(mockReferrerRepository)
    }

    @Test
    fun `invoke should save referrer data to repository`() = runTest {
        val referrerData = ReferrerData(
            utmSource = "pera_website",
            utmMedium = "organic",
            utmCampaign = "download_app",
            utmTerm = "crypto_wallet",
            utmContent = "homepage_button"
        )

        coEvery { mockReferrerRepository.saveReferrerData(any()) } returns Unit

        sut.invoke(referrerData)

        coVerify(exactly = 1) { mockReferrerRepository.saveReferrerData(referrerData) }
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

        coEvery { mockReferrerRepository.saveReferrerData(any()) } returns Unit

        sut.invoke(emptyReferrerData)

        coVerify(exactly = 1) { mockReferrerRepository.saveReferrerData(emptyReferrerData) }
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

        coEvery { mockReferrerRepository.saveReferrerData(any()) } returns Unit

        sut.invoke(partialReferrerData)

        coVerify(exactly = 1) { mockReferrerRepository.saveReferrerData(partialReferrerData) }
    }
}
