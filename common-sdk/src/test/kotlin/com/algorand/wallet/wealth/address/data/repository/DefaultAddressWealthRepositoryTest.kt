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

package com.algorand.wallet.wealth.address.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.address.data.api.AddressWealthApiService
import com.algorand.wallet.wealth.address.data.mapper.AddressWealthMapper
import com.algorand.wallet.wealth.address.domain.model.AddressWealth
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResults
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAddressWealthRepositoryTest {

    private val addressWealthApiService: AddressWealthApiService = mockk(relaxed = true)
    private val addressWealthMapper: AddressWealthMapper = mockk(relaxed = true)
    private val periodRequestMapper: WalletWealthPeriodRequestMapper = mockk {
        every { invoke(period = PERIOD) } returns PERIOD_QUERY
    }

    private val sut = DefaultAddressWealthRepository(addressWealthApiService, periodRequestMapper, addressWealthMapper)

    @Test
    fun `EXPECT Error WHEN fetching wealth fails`() = runTest {
        coEvery {
            addressWealthApiService.getAddressWealth(
                ADDRESS_QUERY,
                PERIOD_QUERY,
                CURRENCY_QUERY
            )
        } throws Exception()

        val result = sut.getAddressWealth(ADDRESS, PERIOD, CURRENCY)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT mapped result WHEN fetching succeeds`() = runTest {
        coEvery {
            addressWealthApiService.getAddressWealth(
                ADDRESS_QUERY,
                PERIOD_QUERY,
                CURRENCY_QUERY
            )
        } returns WALLET_WEALTH_RESPONSE
        every { addressWealthMapper.map(WALLET_WEALTH_RESPONSE) } returns ADDRESS_WEALTH

        val result = sut.getAddressWealth(ADDRESS, PERIOD, CURRENCY)

        val expected = PeraResult.Success(ADDRESS_WEALTH)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "address1"
        const val ADDRESS_QUERY = "address1"
        const val CURRENCY = "USD"
        const val CURRENCY_QUERY = "USD"
        val PERIOD: WalletWealthPeriod = peraFixture()
        val PERIOD_QUERY: String = peraFixture()

        val WALLET_WEALTH_RESPONSE = peraFixture<WalletChartResponseResults>()
        val ADDRESS_WEALTH = peraFixture<AddressWealth>()
    }
}
