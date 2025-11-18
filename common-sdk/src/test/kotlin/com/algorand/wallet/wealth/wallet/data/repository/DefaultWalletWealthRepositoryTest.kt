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

package com.algorand.wallet.wealth.wallet.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.data.api.WalletWealthApiService
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthMapper
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.data.model.WalletChartRequest
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResults
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealth
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultWalletWealthRepositoryTest {

    private val walletWealthApiService: WalletWealthApiService = mockk(relaxed = true)
    private val walletWealthMapper: WalletWealthMapper = mockk(relaxed = true)
    private val periodRequestMapper: WalletWealthPeriodRequestMapper = mockk {
        every { invoke(period = PERIOD) } returns PERIOD_QUERY
    }

    private val sut = DefaultWalletWealthRepository(walletWealthApiService, walletWealthMapper, periodRequestMapper)

    @Test
    fun `EXPECT Error WHEN fetching wealth fails`(): TestResult = runTest {
        coEvery {
            walletWealthApiService.getWalletWealth(
                WalletChartRequest(
                    ADDRESSES,
                    PERIOD_QUERY,
                    CURRENCY
                )
            )
        } throws Exception()

        val result = sut.getWalletWealth(ADDRESSES, PERIOD, CURRENCY)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT mapped result WHEN fetching succeeds`(): TestResult = runTest {
        coEvery {
            walletWealthApiService.getWalletWealth(
                WalletChartRequest(
                    ADDRESSES,
                    PERIOD_QUERY,
                    CURRENCY
                )
            )
        } returns WALLET_WEALTH_RESPONSE
        every { walletWealthMapper.map(WALLET_WEALTH_RESPONSE) } returns WALLET_WEALTH

        val result = sut.getWalletWealth(ADDRESSES, PERIOD, CURRENCY)

        val expected = PeraResult.Success(WALLET_WEALTH)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS_1 = "address1"
        const val ADDRESS_2 = "address2"

        const val CURRENCY = "USD"
        val ADDRESSES = listOf(ADDRESS_1, ADDRESS_2)
        val PERIOD: WalletWealthPeriod = peraFixture()
        val PERIOD_QUERY: String = peraFixture()

        val WALLET_WEALTH_RESPONSE = peraFixture<WalletChartResponseResults>()
        val WALLET_WEALTH = peraFixture<WalletWealth>()
    }
}
