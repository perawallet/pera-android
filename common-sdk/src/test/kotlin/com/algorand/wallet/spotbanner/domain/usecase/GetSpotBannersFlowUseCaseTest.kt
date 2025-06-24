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

package com.algorand.wallet.spotbanner.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyAuthAddressWithBalance
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetSpotBannersFlowUseCaseTest {

    private val isThereAnyAuthAddressWithBalance: IsThereAnyAuthAddressWithBalance = mockk()
    private val spotBannerRepository: SpotBannerRepository = mockk(relaxed = true)

    private val sut = GetSpotBannersFlowUseCase(isThereAnyAuthAddressWithBalance, spotBannerRepository)

    @Test
    fun `EXPECT spot banners without backup passphrase banner WHEN there is no auth address with balance`() = runTest {
        val cacheFlow = MutableStateFlow<List<SpotBanner>>(listOf(BANNER_1, BANNER_2))
        coEvery { isThereAnyAuthAddressWithBalance() } returns false
        coEvery { spotBannerRepository.getSpotBannerFlow() } returns cacheFlow

        val testObserver = sut().test()
        cacheFlow.value = listOf(BANNER_2)

        testObserver.assertValueHistory(
            listOf(BANNER_1, BANNER_2),
            listOf(BANNER_2)
        )
    }

    @Test
    fun `EXPECT spot banners with backup passphrase banner WHEN there is an auth address with balance`() = runTest {
        val cacheFlow = MutableStateFlow<List<SpotBanner>>(emptyList())
        coEvery { isThereAnyAuthAddressWithBalance() } returns true
        coEvery { spotBannerRepository.getSpotBannerFlow() } returns cacheFlow

        val testObserver = sut().test()
        cacheFlow.value = listOf(BANNER_2)

        testObserver.assertValueHistory(
            listOf(SpotBanner.BackupPassphrase),
            listOf(SpotBanner.BackupPassphrase, BANNER_2)
        )
    }

    private companion object {
        val BANNER_1 = peraFixture<SpotBanner>()
        val BANNER_2 = peraFixture<SpotBanner>()
    }
}
