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

package com.algorand.wallet.banner.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.banner.domain.repository.BannerRepository
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitializeBannersUseCaseTest {

    private val bannerRepository: BannerRepository = mockk(relaxed = true)

    private val sut = InitializeBannersUseCase(bannerRepository)

    @Test
    fun `EXPECT cache to be cleared and nothing else WHEN get banners returns error`(): TestResult = runTest {
        coEvery { bannerRepository.getBanners(DEVICE_ID) } returns PeraResult.Error(Exception())

        sut(DEVICE_ID)

        coVerify { bannerRepository.clearBannerCache() }
        coVerify(exactly = 0) { bannerRepository.cacheBanner(any()) }
    }

    @Test
    fun `EXPECT dismissed banners to be filtered and first banner to be cached`(): TestResult = runTest {
        val banners = listOf(BANNER_1, BANNER_2, BANNER_3)
        coEvery { bannerRepository.getBanners(DEVICE_ID) } returns PeraResult.Success(banners)
        coEvery { bannerRepository.getDismissedBannerIdList() } returns listOf(BANNER_1.bannerId)

        sut(DEVICE_ID)

        coVerify { bannerRepository.clearBannerCache() }
        coVerify { bannerRepository.cacheBanner(BANNER_2) }
    }

    private companion object {
        const val DEVICE_ID = "test-device-id"

        val BANNER_1 = peraFixture<Banner>().copy(bannerId = 1L)
        val BANNER_2 = peraFixture<Banner>().copy(bannerId = 2L)
        val BANNER_3 = peraFixture<Banner>().copy(bannerId = 3L)
    }
}
