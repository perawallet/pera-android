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

import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DismissSpotBannerUseCaseTest {

    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId = mockk()
    private val spotBannerRepository: SpotBannerRepository = mockk(relaxed = true)

    private val sut = DismissSpotBannerUseCase(getSelectedNodeDeviceId, spotBannerRepository)

    @Test
    fun `EXPECT no action WHEN device id is null`(): TestResult = runTest {
        every { getSelectedNodeDeviceId() } returns null

        sut(BANNER_ID)

        coVerify(exactly = 0) { spotBannerRepository.dismissBanner(any(), any()) }
    }

    @Test
    fun `EXPECT banner to be dismissed WHEN device id exists`(): TestResult = runTest {
        val deviceId = "device-id"
        every { getSelectedNodeDeviceId() } returns deviceId

        sut(BANNER_ID)

        coVerify { spotBannerRepository.dismissBanner(deviceId, BANNER_ID) }
    }

    private companion object {
        const val BANNER_ID = 1L
    }
}
