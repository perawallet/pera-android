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
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.LedgerBle
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuotePayload
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuotes
import com.algorand.wallet.swap.domain.repository.SwapRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class GetSwapQuotesUseCaseTest {

    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId = mockk {
        every { this@mockk.invoke() } returns DEVICE_ID
    }
    private val swapRepository: SwapRepository = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk {
        every { invoke(featureToggleKey = LEDGER_DEFLEX_FILTER) } returns false
    }
    private val getSwapQuoteDetails: GetSwapQuoteDetails = mockk {
        coEvery { invoke(quotes = QUOTES) } returns QUOTE_DETAILS
    }
    private val getAccountType: GetAccountType = mockk()

    private val sut = GetSwapQuotesUseCase(
        getSelectedNodeDeviceId,
        swapRepository,
        getSwapQuoteDetails,
        isFeatureToggleEnabled,
        getAccountType
    )

    @Test
    fun `EXPECT error WHEN quote list is empty`() = runTest {
        coEvery { getAccountType(any()) } returns AccountType.Algo25
        coEvery { swapRepository.getSwapQuotes(REQUEST) } returns PeraResult.Success(emptyList())

        val result = sut(PAYLOAD)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN repository call fails`() = runTest {
        coEvery { swapRepository.getSwapQuotes(REQUEST) } returns PeraResult.Error(Exception())

        val result = sut(PAYLOAD)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT best offer preselected quotes WHEN there are multiple quotes`() = runTest {
        coEvery { getAccountType(any()) } returns AccountType.Algo25
        coEvery { swapRepository.getSwapQuotes(REQUEST) } returns PeraResult.Success(QUOTES)

        val result = sut(PAYLOAD)

        val expected = PeraResult.Success(
            SwapQuotes(selectedQuoteId = BEST_QUOTE_ID, bestOfferQuoteId = BEST_QUOTE_ID, quotes = QUOTE_DETAILS)
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty device id WHEN does not exist`() = runTest {
        coEvery { getAccountType(any()) } returns AccountType.Algo25
        every { getSelectedNodeDeviceId() } returns null
        val request = REQUEST.copy(deviceId = "")

        coEvery { swapRepository.getSwapQuotes(request) } returns PeraResult.Success(QUOTES)

        val result = sut(PAYLOAD)

        val expected = PeraResult.Success(
            SwapQuotes(selectedQuoteId = BEST_QUOTE_ID, bestOfferQuoteId = BEST_QUOTE_ID, quotes = QUOTE_DETAILS)
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT deflex filtered out WHEN ledger filter is enabled and account type is LedgerBle`() = runTest {
        val randomQuote = peraFixture<SwapQuoteV2>()
        val deflexQuote = peraFixture<SwapQuoteV2>().copy(
            provider = peraFixture<SwapQuoteProvider>().copy(name = "deflex")
        )
        val randomQuoteDetail = peraFixture<SwapQuoteDetail>()
        every { isFeatureToggleEnabled(LEDGER_DEFLEX_FILTER) } returns true
        coEvery { getAccountType(ADDRESS) } returns LedgerBle
        coEvery { swapRepository.getSwapQuotes(REQUEST) } returns PeraResult.Success(listOf(randomQuote, deflexQuote))
        coEvery { getSwapQuoteDetails.invoke(listOf(randomQuote)) } returns listOf(randomQuoteDetail)

        val result = sut(PAYLOAD)

        val expected = PeraResult.Success(
            SwapQuotes(
                selectedQuoteId = randomQuote.quoteId,
                bestOfferQuoteId = randomQuote.quoteId,
                quotes = listOf(randomQuoteDetail)
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT deflex in quotes WHEN ledger filter is enabled and account type is not LedgerBle`() = runTest {
        val randomQuote = peraFixture<SwapQuoteV2>().copy(
            assetOutAmount = peraFixture<SwapQuoteV2.AssetAmount>().copy(amount = BigDecimal.TEN)
        )
        val deflexQuote = peraFixture<SwapQuoteV2>().copy(
            assetOutAmount = peraFixture<SwapQuoteV2.AssetAmount>().copy(amount = BigDecimal.ONE),
            provider = peraFixture<SwapQuoteProvider>().copy(name = "deflex")
        )
        val randomQuoteDetail = peraFixture<SwapQuoteDetail>()
        val deflexQuoteDetail = peraFixture<SwapQuoteDetail>()
        every { isFeatureToggleEnabled(LEDGER_DEFLEX_FILTER) } returns true
        coEvery { getAccountType(ADDRESS) } returns AccountType.Algo25
        coEvery { swapRepository.getSwapQuotes(REQUEST) } returns PeraResult.Success(listOf(randomQuote, deflexQuote))
        coEvery {
            getSwapQuoteDetails.invoke(listOf(randomQuote, deflexQuote))
        } returns listOf(randomQuoteDetail, deflexQuoteDetail)

        val result = sut(PAYLOAD)

        val expected = PeraResult.Success(
            SwapQuotes(
                selectedQuoteId = randomQuote.quoteId,
                bestOfferQuoteId = randomQuote.quoteId,
                quotes = listOf(randomQuoteDetail, deflexQuoteDetail)
            )
        )
        assertEquals(expected, result)
    }

    private companion object {
        val DEVICE_ID = peraFixture<String>()
        val ADDRESS = peraFixture<String>()
        val ASSET_IN = peraFixture<Long>()
        val ASSET_OUT = peraFixture<Long>()
        val AMOUNT = peraFixture<BigInteger>()
        val SLIPPAGE = peraFixture<Double?>()

        val PAYLOAD = SwapQuotePayload(ADDRESS, ASSET_IN, ASSET_OUT, AMOUNT, SLIPPAGE)
        val REQUEST = SwapQuoteRequestPayload(ADDRESS, ASSET_IN, ASSET_OUT, AMOUNT, DEVICE_ID, SLIPPAGE)

        const val BEST_QUOTE_ID = 1L
        val BEST_QUOTE = peraFixture<SwapQuoteV2>().copy(
            quoteId = BEST_QUOTE_ID,
            assetOutAmount = peraFixture<SwapQuoteV2.AssetAmount>().copy(amount = BigDecimal.TEN)
        )
        const val WORST_QUOTE_ID = 2L
        val WORST_QUOTE = peraFixture<SwapQuoteV2>().copy(
            quoteId = WORST_QUOTE_ID,
            assetOutAmount = peraFixture<SwapQuoteV2.AssetAmount>().copy(amount = BigDecimal.ONE)
        )
        val QUOTES = listOf(BEST_QUOTE, WORST_QUOTE)
        val QUOTE_DETAILS = peraFixture<List<SwapQuoteDetail>>()
        const val LEDGER_DEFLEX_FILTER = "enable_ledger_deflex_filter"
    }
}
