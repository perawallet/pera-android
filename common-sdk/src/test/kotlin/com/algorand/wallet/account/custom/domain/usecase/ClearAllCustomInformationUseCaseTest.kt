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

package com.algorand.wallet.account.custom.domain.usecase

import com.algorand.wallet.account.custom.domain.repository.CustomAccountInfoRepository
import com.algorand.wallet.account.custom.domain.repository.CustomHdSeedInfoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ClearAllCustomInformationUseCaseTest {

    private val customAccountInfoRepository: CustomAccountInfoRepository = mockk(relaxed = true)
    private val customHdSeedInfoRepository: CustomHdSeedInfoRepository = mockk(relaxed = true)

    private val sut = ClearAllCustomInformationUseCase(customAccountInfoRepository, customHdSeedInfoRepository)

    @Test
    fun `EXPECT both account and hd seed information to be cleared`() = runTest {
        sut()

        coVerify { customAccountInfoRepository.clearAllInformation() }
        coVerify { customHdSeedInfoRepository.clearAllInformation() }
    }
}
