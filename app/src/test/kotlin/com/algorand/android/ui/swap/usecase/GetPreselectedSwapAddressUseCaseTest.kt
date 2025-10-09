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

package com.algorand.android.ui.swap.usecase

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus.Data
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.test.peraFixture
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.swap.domain.usecase.GetLastUsedSwapAddress
import com.algorand.wallet.swap.domain.usecase.SetLastUsedSwapAddress
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPreselectedSwapAddressUseCaseTest {

    private val getLastUsedSwapAddress: GetLastUsedSwapAddress = mockk()
    private val setLastUsedSwapAddress: SetLastUsedSwapAddress = mockk(relaxed = true)
    private val getAccountLiteCacheData: GetAccountLiteCacheData = mockk()

    private val sut = GetPreselectedSwapAddressUseCase(
        getLastUsedSwapAddress,
        getAccountLiteCacheData,
        setLastUsedSwapAddress
    )

    @Test
    fun `EXPECT last used address WHEN exists in cache and can sign transaction`() = runTest {
        coEvery { getLastUsedSwapAddress() } returns ALGO_25_ADDRESS
        coEvery { getAccountLiteCacheData() } returns Data(emptyList(), ACCOUNT_LITES)

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
    }

    @Test
    fun `EXPECT first valid address WHEN last used address is not cached`() = runTest {
        val accountLiteCacheData = peraFixture<Data>().copy(
            accountLites = mapOf(WATCH_ADDRESS to WATCH_ACCOUNT_LITE, ALGO_25_ADDRESS to ALGO_25_LITE)
        )
        coEvery { getLastUsedSwapAddress() } returns null
        coEvery { getAccountLiteCacheData() } returns accountLiteCacheData

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
        coVerify { setLastUsedSwapAddress(ALGO_25_ADDRESS) }
    }

    @Test
    fun `EXPECT first valid address WHEN last used address is not valid`() = runTest {
        val algo25Lite = ALGO_25_LITE.copy(cachedInfo = null)
        val ledgerLite = peraFixture<AccountLite>().copy(
            address = "LEDGER_ADDRESS",
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(
                type = AccountType.LedgerBle
            )
        )
        val accountLiteCacheData = peraFixture<Data>().copy(
            accountLites = mapOf(
                WATCH_ADDRESS to WATCH_ACCOUNT_LITE,
                ALGO_25_ADDRESS to algo25Lite,
                "LEDGER_ADDRESS" to ledgerLite
            )
        )
        coEvery { getLastUsedSwapAddress() } returns ALGO_25_ADDRESS
        coEvery { getAccountLiteCacheData() } returns accountLiteCacheData

        val result = sut()

        assertEquals("LEDGER_ADDRESS", result)
    }

    @Test
    fun `EXPECT last used address to be updated WHEN cache is not valid and there is valid address`() = runTest {
        coEvery { getLastUsedSwapAddress() } returns "INVALID_ADDRESS"
        coEvery { getAccountLiteCacheData() } returns Data(emptyList(), ACCOUNT_LITES)

        val result = sut()

        assertEquals(ALGO_25_ADDRESS, result)
        coVerify { setLastUsedSwapAddress(ALGO_25_ADDRESS) }
    }

    @Test
    fun `EXPECT null WHEN no valid address exists`() = runTest {
        val accountLiteCacheData = peraFixture<Data>().copy(
            accountLites = mapOf(WATCH_ADDRESS to WATCH_ACCOUNT_LITE)
        )
        coEvery { getLastUsedSwapAddress() } returns null
        coEvery { getAccountLiteCacheData() } returns accountLiteCacheData

        val result = sut()

        assertEquals(null, result)
    }

    private companion object {
        const val ALGO_25_ADDRESS = "ALGO_25_ADDRESS"
        val ALGO_25_LITE = peraFixture<AccountLite>().copy(
            address = ALGO_25_ADDRESS,
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.Algo25)
        )

        const val WATCH_ADDRESS = "WATCH_ADDRESS"
        val WATCH_ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            address = WATCH_ADDRESS,
            cachedInfo = peraFixture<AccountLite.CachedInfo>().copy(type = AccountType.NoAuth)
        )

        val ACCOUNT_LITES = mapOf(ALGO_25_ADDRESS to ALGO_25_LITE, WATCH_ADDRESS to WATCH_ACCOUNT_LITE)
    }
}
