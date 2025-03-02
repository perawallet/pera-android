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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsAssetOwnedByAccountUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mockk()

    private val sut = IsAssetOwnedByAccountUseCase(getAccountInformation)

    @Test
    fun `EXPECT false WHEN invoked by address and account information is null`() = runTest {
        coEvery { getAccountInformation(ADDRESS) } returns null

        val result = sut(ADDRESS, ASSET_ID)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN invoked by address, asset id is ALGO_ID and account amount is greater than zero`() = runTest {
        val accountInfo = ACCOUNT_INFO.copy(amount = BigInteger.ONE)
        coEvery { getAccountInformation(ADDRESS) } returns accountInfo

        val result = sut(ADDRESS, ALGO_ID)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is ALGO_ID and account amount is zero`() = runTest {
        val accountInfo = ACCOUNT_INFO.copy(amount = BigInteger.ZERO)
        coEvery { getAccountInformation(ADDRESS) } returns accountInfo

        val result = sut(ADDRESS, ALGO_ID)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN invoked by address, asset id is not ALGO_ID and account has asset holdings with amount greater than zero`() =
        runTest {
            val assetHolding = ASSET_HOLDING.copy(amount = BigInteger.ONE)
            val accountInfo = ACCOUNT_INFO.copy(assetHoldings = listOf(assetHolding))
            coEvery { getAccountInformation(ADDRESS) } returns accountInfo

            val result = sut(ADDRESS, ASSET_ID)

            assertTrue(result)
        }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is not ALGO_ID and account has asset holdings with amount zero`() =
        runTest {
            val assetHolding = ASSET_HOLDING.copy(amount = BigInteger.ZERO)
            val accountInfo = ACCOUNT_INFO.copy(assetHoldings = listOf(assetHolding))
            coEvery { getAccountInformation(ADDRESS) } returns accountInfo

            val result = sut(ADDRESS, ASSET_ID)

            assertFalse(result)
        }

    @Test
    fun `EXPECT false WHEN invoked by address, asset id is not ALGO_ID and account has no asset holdings`() = runTest {
        val assetHolding = peraFixture<AssetHolding>().copy(assetId = 9)
        val accountInfo = ACCOUNT_INFO.copy(assetHoldings = listOf(assetHolding))
        coEvery { getAccountInformation(ADDRESS) } returns accountInfo

        val result = sut(ADDRESS, ASSET_ID)

        assertFalse(result)
    }

    private companion object {
        const val ADDRESS = "address"
        const val ASSET_ID = 1L
        const val ALGO_ID = -7L

        val ACCOUNT_INFO = peraFixture<AccountInformation>()
        val ASSET_HOLDING = peraFixture<AssetHolding>().copy(assetId = ASSET_ID)
    }
}
