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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.swap.domain.repository.SwapRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPreselectedSwapAddressUseCaseTest {

    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes = mockk()
    private val swapRepository: SwapRepository = mockk(relaxed = true)

    private val sut = GetPreselectedSwapAddressUseCase(getAllAccountOrderIndexes, swapRepository)

    @Test
    fun `EXPECT cached address WHEN last used address exists`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns ADDRESS

        val result = sut()

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT first address to be returned and cached WHEN last used address does not exist`() = runTest {
        val firstAddress = "firstAddress"
        val accountOrderIndexes = listOf(
            AccountOrderIndex(ADDRESS, 1),
            AccountOrderIndex(firstAddress, 0)
        )
        coEvery { swapRepository.getLastUsedSwapAddress() } returns null
        coEvery { getAllAccountOrderIndexes() } returns accountOrderIndexes

        val result = sut()

        assertEquals(firstAddress, result)
        coVerify { swapRepository.setLastUsedSwapAddress(firstAddress) }
    }

    private companion object {
        const val ADDRESS = "address"
    }
}
