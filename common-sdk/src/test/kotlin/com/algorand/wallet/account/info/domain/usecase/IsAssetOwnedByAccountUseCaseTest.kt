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

package com.algorand.wallet.account.info.domain.usecase

import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsAssetOwnedByAccountUseCaseTest {

    private val getAccountAlgoBalance: GetAccountAlgoBalance = mockk()
    private val getAccountAssetHoldingAmount: GetAccountAssetHoldingAmount = mockk()

    private val sut = IsAssetOwnedByAccountUseCase(getAccountAlgoBalance, getAccountAssetHoldingAmount)

    @Test
    fun `EXPECT false WHEN invoked by address and account information is null`() = runTest {
        coEvery { getAccountAssetHoldingAmount(ADDRESS, ASSET_ID) } returns null

        val result = sut(ADDRESS, ASSET_ID)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN invoked by address, asset id is ALGO_ID and account amount is greater than zero`() = runTest {
        coEvery { getAccountAlgoBalance(ADDRESS) } returns BigInteger.TWO

        val result = sut(ADDRESS, ALGO_ID)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is ALGO_ID and account amount is zero`() = runTest {
        coEvery { getAccountAlgoBalance(ADDRESS) } returns BigInteger.ZERO

        val result = sut(ADDRESS, ALGO_ID)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN invoked by address, asset id is not ALGO_ID and account has asset holdings with amount greater than zero`() =
        runTest {
            coEvery { getAccountAssetHoldingAmount(ADDRESS, ASSET_ID) } returns BigInteger.ONE

            val result = sut(ADDRESS, ASSET_ID)

            assertTrue(result)
        }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is not ALGO_ID and account has asset holdings with amount zero`() =
        runTest {
            coEvery { getAccountAssetHoldingAmount(ADDRESS, ASSET_ID) } returns BigInteger.ZERO

            val result = sut(ADDRESS, ASSET_ID)

            assertFalse(result)
        }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is not ALGO_ID and account has no asset holdings`() = runTest {
        coEvery { getAccountAssetHoldingAmount(ADDRESS, ASSET_ID) } returns null

        val result = sut(ADDRESS, ASSET_ID)

        assertFalse(result)
    }

    private companion object {
        const val ADDRESS = "address"
        const val ASSET_ID = 1L
        const val ALGO_ID = -7L
    }
}
