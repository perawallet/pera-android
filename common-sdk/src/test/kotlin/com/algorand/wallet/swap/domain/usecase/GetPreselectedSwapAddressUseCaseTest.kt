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

import com.algorand.test.peraFixture
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.wallet.swap.domain.repository.SwapRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPreselectedSwapAddressUseCaseTest {

    private val getAccountsDetails: GetAccountsDetails = mockk()
    private val swapRepository: SwapRepository = mockk(relaxed = true)

    private val sut = GetPreselectedSwapAddressUseCase(swapRepository, getAccountsDetails)

    @Test
    fun `EXPECT last used address WHEN exists in cache and can sign transaction`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns ALGO_25_ADDRESS
        coEvery { getAccountsDetails() } returns listOf(ALGO_25_DETAIL, WATCH_DETAIL)

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
    }

    @Test
    fun `EXPECT first valid address WHEN last used address is not cached`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns null
        coEvery { getAccountsDetails() } returns listOf(ALGO_25_DETAIL, WATCH_DETAIL)

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
        coEvery { swapRepository.setLastUsedSwapAddress(ALGO_25_ADDRESS) }
    }

    @Test
    fun `EXPECT first valid address WHEN last used address is not valid`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns "INVALID_ADDRESS"
        val algo25 = ALGO_25_DETAIL.copy(customAccountInfo = null)
        val watch = WATCH_DETAIL.copy(customAccountInfo = WATCH_DETAIL.customAccountInfo?.copy(orderIndex = 0))
        val ledger = peraFixture<AccountDetail>().copy(
            address = "LEDGER_ADDRESS",
            customAccountInfo = peraFixture<CustomAccountInfo>().copy(orderIndex = 3),
            accountType = AccountType.LedgerBle
        )
        coEvery { getAccountsDetails() } returns listOf(algo25, ledger, watch)

        val result = sut()

        assertEquals("LEDGER_ADDRESS", result)
        coEvery { swapRepository.setLastUsedSwapAddress("LEDGER_ADDRESS") }
    }

    @Test
    fun `EXPECT last used address to be updated WHEN cache is not valid and there is valid address`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns "INVALID_ADDRESS"
        coEvery { getAccountsDetails() } returns listOf(ALGO_25_DETAIL, WATCH_DETAIL)

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
        coVerify { swapRepository.setLastUsedSwapAddress(ALGO_25_ADDRESS) }
    }

    @Test
    fun `EXPECT null WHEN no valid address exists`() = runTest {
        coEvery { swapRepository.getLastUsedSwapAddress() } returns null
        coEvery { getAccountsDetails() } returns listOf(WATCH_DETAIL)

        val result = sut()

        assertEquals(null, result)
        coVerify(exactly = 0) { swapRepository.setLastUsedSwapAddress(any()) }
    }

    private companion object {

        const val ALGO_25_ADDRESS = "ALGO_25_ADDRESS"
        val ALGO_25_DETAIL = peraFixture<AccountDetail>().copy(
            address = ALGO_25_ADDRESS,
            customAccountInfo = peraFixture<CustomAccountInfo>().copy(orderIndex = 2),
            accountType = AccountType.Algo25
        )

        const val WATCH_ADDRESS = "WATCH_ADDRESS"
        val WATCH_DETAIL = peraFixture<AccountDetail>().copy(
            address = WATCH_ADDRESS,
            customAccountInfo = peraFixture<CustomAccountInfo>().copy(orderIndex = 1),
            accountType = AccountType.NoAuth
        )
    }
}
