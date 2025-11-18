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

package com.algorand.wallet.wealth.asset.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.asset.data.api.AssetBalanceHistoryApiService
import com.algorand.wallet.wealth.asset.data.mapper.AssetBalanceHistoryMapper
import com.algorand.wallet.wealth.asset.data.model.AssetBalanceHistoryResponseResults
import com.algorand.wallet.wealth.asset.domain.model.AssetBalanceHistory
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAssetBalanceHistoryRepositoryTest {

    private val assetBalanceHistoryApiService: AssetBalanceHistoryApiService = mockk(relaxed = true)
    private val assetBalanceHistoryMapper: AssetBalanceHistoryMapper = mockk(relaxed = true)
    private val periodRequestMapper: WalletWealthPeriodRequestMapper = mockk {
        every { invoke(period = PERIOD) } returns PERIOD_QUERY
    }

    private val sut =
        DefaultAssetBalanceHistoryRepository(
            assetBalanceHistoryApiService,
            periodRequestMapper,
            assetBalanceHistoryMapper
        )

    @Test
    fun `EXPECT Error WHEN fetching asset balance history fails`(): TestResult = runTest {
        coEvery {
            assetBalanceHistoryApiService.getAssetBalanceHistory(
                ADDRESS_QUERY,
                ASSET_ID_QUERY,
                PERIOD_QUERY,
                CURRENCY_QUERY
            )
        } throws Exception()

        val result = sut.getAssetBalanceHistory(ADDRESS, ASSET_ID, PERIOD, CURRENCY)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT mapped result WHEN fetching succeeds`(): TestResult = runTest {
        coEvery {
            assetBalanceHistoryApiService.getAssetBalanceHistory(
                ADDRESS_QUERY,
                ASSET_ID_QUERY,
                PERIOD_QUERY,
                CURRENCY_QUERY
            )
        } returns ASSET_BALANCE_HISTORY_RESPONSE
        every { assetBalanceHistoryMapper.map(ASSET_BALANCE_HISTORY_RESPONSE) } returns ASSET_BALANCE_HISTORY

        val result = sut.getAssetBalanceHistory(ADDRESS, ASSET_ID, PERIOD, CURRENCY)

        val expected = PeraResult.Success(ASSET_BALANCE_HISTORY)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "address1"
        const val CURRENCY = "USD"
        const val ASSET_ID = 123L
        const val ADDRESS_QUERY = "address1"
        const val CURRENCY_QUERY = "USD"
        const val ASSET_ID_QUERY = 123L
        val PERIOD: WalletWealthPeriod = peraFixture()
        val PERIOD_QUERY: String = peraFixture()

        val ASSET_BALANCE_HISTORY_RESPONSE = peraFixture<AssetBalanceHistoryResponseResults>()
        val ASSET_BALANCE_HISTORY = peraFixture<AssetBalanceHistory>()
    }
}
