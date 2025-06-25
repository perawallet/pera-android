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
import com.algorand.wallet.account.detail.domain.model.AccountType.Algo25
import com.algorand.wallet.account.detail.domain.model.AccountType.HdKey
import com.algorand.wallet.account.detail.domain.model.AccountType.LedgerBle
import com.algorand.wallet.account.detail.domain.model.AccountType.NoAuth
import com.algorand.wallet.account.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import com.algorand.wallet.spotbanner.domain.model.SpotBannerFlowData
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetSpotBannersFlowUseCaseTest {

    private val spotBannerRepository: SpotBannerRepository = mockk(relaxed = true)

    private val sut = GetSpotBannersFlowUseCase(spotBannerRepository)

    @Test
    fun `EXPECT banners without backup passphrase WHEN there is not any auth address with balance`() = runTest {
        val cacheFlow = MutableStateFlow<List<SpotBanner>>(listOf(BANNER_1, BANNER_2))
        val data = listOf(
            SpotBannerFlowData("address1", isBackedUp = true, type = null, primaryBalance = null),
            SpotBannerFlowData("address1", isBackedUp = false, type = null, primaryBalance = null),
            SpotBannerFlowData("address2", isBackedUp = true, type = NoAuth, primaryBalance = null),
            SpotBannerFlowData("address2", isBackedUp = false, type = Algo25, primaryBalance = null),
            SpotBannerFlowData("address3", isBackedUp = true, type = NoAuth, primaryBalance = BigDecimal.TEN),
            SpotBannerFlowData("address3", isBackedUp = true, type = Algo25, primaryBalance = BigDecimal.TEN)
        )
        coEvery { spotBannerRepository.getSpotBannerFlow() } returns cacheFlow

        val testObserver = sut(data).test()
        cacheFlow.value = listOf(BANNER_2)

        testObserver.assertValueHistory(
            listOf(BANNER_1, BANNER_2),
            listOf(BANNER_2)
        )
    }

    @Test
    fun `EXPECT banners with backup passphrase WHEN there is not backed up auth address with balance`() = runTest {
        val cacheFlow = MutableStateFlow<List<SpotBanner>>(emptyList())
        val data = listOf(
            SpotBannerFlowData("address1", isBackedUp = false, type = Algo25, primaryBalance = ONE),
            SpotBannerFlowData("address1", isBackedUp = false, type = HdKey, primaryBalance = ONE),
            SpotBannerFlowData("address1", isBackedUp = false, type = LedgerBle, primaryBalance = ONE),
            SpotBannerFlowData("address1", isBackedUp = false, type = RekeyedAuth, primaryBalance = ONE),
        )
        coEvery { spotBannerRepository.getSpotBannerFlow() } returns cacheFlow

        val testObserver = sut(data).test()
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
