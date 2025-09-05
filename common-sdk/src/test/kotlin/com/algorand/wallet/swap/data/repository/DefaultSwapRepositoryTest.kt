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

package com.algorand.wallet.swap.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.InMemoryCachedObject
import com.algorand.wallet.foundation.cache.PersistentCache
import com.algorand.wallet.swap.data.mapper.AvailableSwapAssetMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteProviderMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteRequestBodyMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteTransactionMapper
import com.algorand.wallet.swap.data.mapper.TopSwapPairsMapper
import com.algorand.wallet.swap.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.wallet.swap.data.model.CreateSwapQuoteTransactionsResponse
import com.algorand.wallet.swap.data.model.SwapPeraFeeRequestBody
import com.algorand.wallet.swap.data.model.SwapPeraFeeResponse
import com.algorand.wallet.swap.data.model.SwapQuoteExceptionRequestBody
import com.algorand.wallet.swap.data.model.SwapQuoteProviderResponse
import com.algorand.wallet.swap.data.model.SwapQuoteProvidersResponse
import com.algorand.wallet.swap.data.model.SwapQuoteRequestBody
import com.algorand.wallet.swap.data.model.SwapQuoteResponse
import com.algorand.wallet.swap.data.model.SwapQuoteResultResponse
import com.algorand.wallet.swap.data.model.SwapQuoteTransactionResponse
import com.algorand.wallet.swap.data.model.TopSwapPairsResponse
import com.algorand.wallet.swap.data.service.SwapApiService
import com.algorand.wallet.swap.domain.model.SwapPeraFee
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteTransaction
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultSwapRepositoryTest {

    private val swapApiService: SwapApiService = mockk()
    private val lastUsedAddressCache: PersistentCache<String> = mockk(relaxed = true)
    private val quoteTransactionMapper: SwapQuoteTransactionMapper = mockk()
    private val quoteRequestMapper: SwapQuoteRequestBodyMapper = mockk()
    private val quoteMapper: SwapQuoteMapper = mockk()
    private val availableSwapAssetMapper: AvailableSwapAssetMapper = mockk()
    private val providersCache: InMemoryCachedObject<List<SwapQuoteProvider>> = mockk(relaxed = true)
    private val swapQuoteProviderMapper: SwapQuoteProviderMapper = mockk()
    private val topSwapPairsMapper: TopSwapPairsMapper = mockk()

    private val sut = DefaultSwapRepository(
        swapApiService,
        lastUsedAddressCache,
        quoteTransactionMapper,
        quoteRequestMapper,
        quoteMapper,
        swapQuoteProviderMapper,
        availableSwapAssetMapper,
        providersCache,
        topSwapPairsMapper
    )

    @Test
    fun `EXPECT error WHEN get swap quote is called but quote providers api call fails`() = runTest {
        every { providersCache.get() } returns null

        val result = sut.getSwapQuotes(SWAP_REQUEST_PAYLOAD)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN get swap quotes api call fails`() = runTest {
        every { providersCache.get() } returns CACHED_PROVIDERS
        coEvery { swapApiService.getSwapQuote(SWAP_REQUEST_BODY) } throws Exception()
        every { quoteRequestMapper(SWAP_REQUEST_PAYLOAD, CACHED_PROVIDERS) } returns SWAP_REQUEST_BODY

        val result = sut.getSwapQuotes(SWAP_REQUEST_PAYLOAD)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN swap quotes are empty`() = runTest {
        every { providersCache.get() } returns CACHED_PROVIDERS
        coEvery { swapApiService.getSwapQuote(SWAP_REQUEST_BODY) } returns SWAP_QUOTE_RESULT_RESPONSE
        every { quoteRequestMapper(SWAP_REQUEST_PAYLOAD, CACHED_PROVIDERS) } returns SWAP_REQUEST_BODY
        every { quoteMapper(SWAP_QUOTE_RESPONSE, CACHED_PROVIDERS) } returns null

        val result = sut.getSwapQuotes(SWAP_REQUEST_PAYLOAD)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT swap quotes WHEN api call returns valid quotes`() = runTest {
        every { providersCache.get() } returns CACHED_PROVIDERS
        coEvery { swapApiService.getSwapQuote(SWAP_REQUEST_BODY) } returns SWAP_QUOTE_RESULT_RESPONSE
        every { quoteRequestMapper(SWAP_REQUEST_PAYLOAD, CACHED_PROVIDERS) } returns SWAP_REQUEST_BODY
        every { quoteMapper(SWAP_QUOTE_RESPONSE, CACHED_PROVIDERS) } returns SWAP_QUOTE

        val result = sut.getSwapQuotes(SWAP_REQUEST_PAYLOAD)

        assertEquals(PeraResult.Success(listOf(SWAP_QUOTE)), result)
    }

    @Test
    fun `EXPECT providers to be cached WHEN get swap quote is called the cache is empty`() = runTest {
        coEvery { swapApiService.getSwapQuoteProviders() } returns PROVIDERS_RESPONSE
        coEvery { swapApiService.getSwapQuote(SWAP_REQUEST_BODY) } returns SWAP_QUOTE_RESULT_RESPONSE
        every { providersCache.get() } returnsMany listOf(null, listOf(PROVIDER))
        every { swapQuoteProviderMapper.invoke(PROVIDER_RESPONSE) } returns PROVIDER
        every { quoteRequestMapper(SWAP_REQUEST_PAYLOAD, listOf(PROVIDER)) } returns SWAP_REQUEST_BODY
        every { quoteMapper(SWAP_QUOTE_RESPONSE, listOf(PROVIDER)) } returns SWAP_QUOTE

        val result = sut.getSwapQuotes(SWAP_REQUEST_PAYLOAD)

        assertEquals(PeraResult.Success(listOf(SWAP_QUOTE)), result)
        verify { providersCache.put(listOf(PROVIDER)) }
    }

    @Test
    fun `EXPECT error WHEN get Pera fee api call fails`() = runTest {
        coEvery { swapApiService.getPeraFee(SWAP_PERA_FEE_REQUEST_BODY) } throws Exception()

        val result = sut.getPeraFee(PERA_FEE_ASSET_ID, PERA_FEE_AMOUNT)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT Pera fee WHEN api call returns valid fee`() = runTest {
        coEvery { swapApiService.getPeraFee(SWAP_PERA_FEE_REQUEST_BODY) } returns SwapPeraFeeResponse(BigInteger.ONE)

        val result = sut.getPeraFee(PERA_FEE_ASSET_ID, PERA_FEE_AMOUNT)

        val expected = SwapPeraFee(BigInteger.ONE)
        assertEquals(PeraResult.Success(expected), result)
    }

    @Test
    fun `EXPECT error WHEN create quote transactions api call fails`() = runTest {
        coEvery { swapApiService.getQuoteTransactions(CreateSwapQuoteTransactionsRequestBody(1L)) } throws Exception()

        val result = sut.createQuoteTransactions(1L)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN quote transactions group is null`() = runTest {
        coEvery { swapApiService.getQuoteTransactions(CREATE_QUOTE_REQUEST) } returns CREATE_QUOTE_RESPONSE.copy(null)

        val result = sut.createQuoteTransactions(CREATE_QUOTE_ID)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN quote transactions group is empty`() = runTest {
        coEvery { swapApiService.getQuoteTransactions(CREATE_QUOTE_REQUEST) } returns CREATE_QUOTE_RESPONSE.copy(
            emptyList()
        )

        val result = sut.createQuoteTransactions(CREATE_QUOTE_ID)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN quote transactions mapping fails`() = runTest {
        coEvery { swapApiService.getQuoteTransactions(CREATE_QUOTE_REQUEST) } returns CREATE_QUOTE_RESPONSE
        every { quoteTransactionMapper(QUOTE_TXN_RESPONSE) } returns null

        val result = sut.createQuoteTransactions(CREATE_QUOTE_ID)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT quote transactions WHEN api call returns valid transactions`() = runTest {
        coEvery { swapApiService.getQuoteTransactions(CREATE_QUOTE_REQUEST) } returns CREATE_QUOTE_RESPONSE
        every { quoteTransactionMapper(QUOTE_TXN_RESPONSE) } returns QUOTE_TRANSACTION

        val result = sut.createQuoteTransactions(CREATE_QUOTE_ID)

        assertEquals(PeraResult.Success(listOf(QUOTE_TRANSACTION)), result)
    }

    @Test
    fun `EXPECT no crash WHEN update quote exception api call fails`() = runTest {
        val body = SwapQuoteExceptionRequestBody("message")
        coEvery { swapApiService.updateSwapQuoteException(CREATE_QUOTE_ID, body) } throws Exception()

        val result = sut.updateSwapQuoteException(CREATE_QUOTE_ID, "message")

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT last used address to be cached WHEN setLastUsedSwapAddress is called`() = runTest {
        sut.setLastUsedSwapAddress("test-address")

        coVerify { lastUsedAddressCache.put("test-address") }
    }

    @Test
    fun `EXPECT cached address WHEN last used address is requested and cache has address`() = runTest {
        every { lastUsedAddressCache.get() } returns "cached-address"

        val address = sut.getLastUsedSwapAddress()

        assertEquals("cached-address", address)
    }

    @Test
    fun `EXPECT null WHEN last used address is requested and cache is empty`() = runTest {
        every { lastUsedAddressCache.get() } returns null

        val address = sut.getLastUsedSwapAddress()

        assertEquals(null, address)
    }

    @Test
    fun `EXPECT error WHEN top swap pairs api call fails`() = runTest {
        coEvery { swapApiService.getTopSwapPairs() } throws Exception()

        val result = sut.getTopSwapPairs()

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT top swap pairs WHEN top swap pairs api call is successful`() = runTest {
        val response = peraFixture<TopSwapPairsResponse>()
        val topSwapPairs = peraFixture<TopSwapPairs>()
        coEvery { swapApiService.getTopSwapPairs() } returns response
        every { topSwapPairsMapper(response) } returns topSwapPairs

        val result = sut.getTopSwapPairs()

        assertEquals(PeraResult.Success(topSwapPairs), result)
    }

    private companion object {
        val SWAP_QUOTE = peraFixture<SwapQuoteV2>()
        val SWAP_REQUEST_PAYLOAD = peraFixture<SwapQuoteRequestPayload>()
        val SWAP_REQUEST_BODY = peraFixture<SwapQuoteRequestBody>()
        val SWAP_QUOTE_RESPONSE = peraFixture<SwapQuoteResponse>()
        val SWAP_QUOTE_RESULT_RESPONSE = peraFixture<SwapQuoteResultResponse>().copy(
            swapQuoteResponseList = listOf(SWAP_QUOTE_RESPONSE)
        )
        val PERA_FEE_AMOUNT: BigInteger = BigInteger.TEN
        const val PERA_FEE_ASSET_ID = 1L
        val SWAP_PERA_FEE_REQUEST_BODY = SwapPeraFeeRequestBody(PERA_FEE_ASSET_ID, PERA_FEE_AMOUNT)
        const val CREATE_QUOTE_ID = 1L
        val CREATE_QUOTE_REQUEST = CreateSwapQuoteTransactionsRequestBody(CREATE_QUOTE_ID)
        val QUOTE_TXN_RESPONSE = peraFixture<SwapQuoteTransactionResponse>()
        val CREATE_QUOTE_RESPONSE = CreateSwapQuoteTransactionsResponse(listOf(QUOTE_TXN_RESPONSE))
        val QUOTE_TRANSACTION = peraFixture<SwapQuoteTransaction>()
        val CACHED_PROVIDERS = peraFixture<List<SwapQuoteProvider>>()
        val PROVIDER_RESPONSE = peraFixture<SwapQuoteProviderResponse>()
        val PROVIDERS_RESPONSE = peraFixture<SwapQuoteProvidersResponse>().copy(results = listOf(PROVIDER_RESPONSE))
        val PROVIDER = peraFixture<SwapQuoteProvider>()
    }
}
