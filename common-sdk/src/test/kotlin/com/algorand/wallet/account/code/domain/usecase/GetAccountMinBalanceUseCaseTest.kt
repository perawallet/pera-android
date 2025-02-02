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

package com.algorand.wallet.account.code.domain.usecase

import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalanceUseCase
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AppStateScheme
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccountMinBalanceUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mockk()

    private val sut = GetAccountMinBalanceUseCase(getAccountInformation)

    @Test
    fun `EXPECT zero WHEN account information is null`() = runTest {
        coEvery { getAccountInformation(ADDRESS) } returns null

        val result = sut(ADDRESS)

        assertEquals(BigInteger.ZERO, result)
    }

    @Test
    fun `EXPECT zero WHEN account is closed`() = runTest {
        coEvery { getAccountInformation(ADDRESS) } returns EMPTY_ACCOUNT

        val result = sut(ADDRESS)

        assertEquals(BigInteger.ZERO, result)
    }

    @Test
    fun `EXPECT min balance`() = runTest {
        val account = EMPTY_ACCOUNT.copy(
            totalAssetsOptedIn = 6,
            totalAppsOptedIn = 4,
            totalCreatedApps = 3,
            appsTotalExtraPages = 2,
            appsTotalSchema = AppStateScheme(3, 4)
        )
        coEvery { getAccountInformation(ADDRESS) } returns account

        val result = sut(ADDRESS)

        val expected = MIN_BALANCE +
            (MIN_BALANCE_TO_KEEP_PER_OPTED_IN_ASSET * 6.toBigInteger()) +
            (MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS * 4.toBigInteger()) +
            (MIN_BALANCE_TO_KEEP_PER_CREATED_APPS * 3.toBigInteger()) +
            (MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_INT * 4.toBigInteger()) +
            (MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_BYTE_SLICE * 3.toBigInteger()) +
            (MIN_BALANCE_TO_KEEP_PER_APP_EXTRA_PAGES * 2.toBigInteger())
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT min balance WHEN account is rekeyed`() = runTest {
        val rekeyedAccount = EMPTY_ACCOUNT.copy(rekeyAdminAddress = "authAddress")
        coEvery { getAccountInformation(ADDRESS) } returns rekeyedAccount

        val result = sut(ADDRESS)

        assertEquals(MIN_BALANCE, result)
    }

    @Test
    fun `EXPECT min balance WHEN account has opt-in assets`() = runTest {
        val account = EMPTY_ACCOUNT.copy(totalAssetsOptedIn = 5)
        coEvery { getAccountInformation(ADDRESS) } returns account

        val result = sut(ADDRESS)

        val expected = MIN_BALANCE + (MIN_BALANCE_TO_KEEP_PER_OPTED_IN_ASSET * 5.toBigInteger())
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT min balance WHEN account has opt-in apps`() = runTest {
        val account = EMPTY_ACCOUNT.copy(totalAppsOptedIn = 5)
        coEvery { getAccountInformation(ADDRESS) } returns account

        val result = sut(ADDRESS)

        val expected = MIN_BALANCE + (MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS * 5.toBigInteger())
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "address"

        val MIN_BALANCE = 100000.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS = 100000.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_OPTED_IN_ASSET = 100000.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_CREATED_APPS = 100000.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_INT = 28500.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_BYTE_SLICE = 50000.toBigInteger()
        val MIN_BALANCE_TO_KEEP_PER_APP_EXTRA_PAGES = 100000.toBigInteger()

        val EMPTY_ACCOUNT = AccountInformation(
            address = ADDRESS,
            amount = BigInteger.ZERO,
            lastFetchedRound = 0,
            rekeyAdminAddress = null,
            totalAppsOptedIn = 0,
            totalAssetsOptedIn = 0,
            totalCreatedApps = 0,
            totalCreatedAssets = 0,
            appsTotalExtraPages = 0,
            appsTotalSchema = null,
            assetHoldings = emptyList(),
            createdAtRound = null
        )
    }
}
