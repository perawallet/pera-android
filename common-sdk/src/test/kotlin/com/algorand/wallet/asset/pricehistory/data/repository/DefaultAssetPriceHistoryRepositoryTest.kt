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

package com.algorand.wallet.asset.pricehistory.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.asset.pricehistory.data.mapper.AssetPriceHistoryMapper
import com.algorand.wallet.asset.pricehistory.data.model.AssetPriceHistoryResponse
import com.algorand.wallet.asset.pricehistory.data.service.AssetPriceHistoryApiService
import com.algorand.wallet.asset.pricehistory.domain.model.AssetPriceHistory
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.data.mapper.WalletWealthPeriodRequestMapper
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAssetPriceHistoryRepositoryTest {

    private val historyApiService: AssetPriceHistoryApiService = mockk()
    private val assetPriceHistoryMapper: AssetPriceHistoryMapper = mockk()
    private val periodRequestMapper: WalletWealthPeriodRequestMapper = mockk()

    private val sut = DefaultAssetPriceHistoryRepository(
        historyApiService,
        assetPriceHistoryMapper,
        periodRequestMapper
    )

    @Test
    fun `EXPECT error WHEN api request fails`() = runTest {
        coEvery { historyApiService.getAssetPriceHistory(ASSET_ID, PERIOD_REQUEST) } throws Exception()

        val result = sut.getAssetPriceHistory(ASSET_ID, PERIOD)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT mapped asset price history`() = runTest {
        coEvery { historyApiService.getAssetPriceHistory(ASSET_ID, PERIOD_REQUEST) } returns PRICE_HISTORY_RESPONSE
        every { assetPriceHistoryMapper(PRICE_HISTORY_ITEM_RESPONSE) } returns PRICE_HISTORY
        every { periodRequestMapper(PERIOD) } returns PERIOD_REQUEST

        val result = sut.getAssetPriceHistory(ASSET_ID, PERIOD)

        val expected = PeraResult.Success(PRICE_HISTORY_RESULT)
        assertEquals(expected, result)
    }

    private companion object {
        const val ASSET_ID = 12345L
        const val PERIOD_REQUEST = "one-day"
        val PERIOD = WalletWealthPeriod.ONE_DAY

        val PRICE_HISTORY_ITEM_RESPONSE = peraFixture<AssetPriceHistoryResponse>()
        val PRICE_HISTORY_RESPONSE = listOf(PRICE_HISTORY_ITEM_RESPONSE)
        val PRICE_HISTORY = peraFixture<AssetPriceHistory>()
        val PRICE_HISTORY_RESULT = listOf(PRICE_HISTORY)
    }
}
