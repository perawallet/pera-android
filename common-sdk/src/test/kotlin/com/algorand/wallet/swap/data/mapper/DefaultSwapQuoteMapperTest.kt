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

package com.algorand.wallet.swap.data.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.swap.data.model.SwapQuoteAssetDetailResponse
import com.algorand.wallet.swap.data.model.SwapQuoteResponse
import com.algorand.wallet.swap.data.model.SwapTypeResponse
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class DefaultSwapQuoteMapperTest {

    private val assetDetailMapper: SwapAssetDetailMapper = mockk {
        every { invoke(response = ASSET_IN_DETAIL_RESPONSE) } returns ASSET_IN_DETAIL
        every { invoke(response = ASSET_OUT_DETAIL_RESPONSE) } returns ASSET_OUT_DETAIL
    }
    private val assetAmountMapper: SwapAssetAmountMapper = mockk {
        every { mapAssetInAmount(response = VALID_RESPONSE) } returns ASSET_IN_AMOUNT
        every { mapAssetOutAmount(response = VALID_RESPONSE) } returns ASSET_OUT_AMOUNT
    }

    private val sut = DefaultSwapQuoteMapper(assetDetailMapper, assetAmountMapper)

    @Test
    fun `EXPECT null WHEN response id id null`() {
        val response = VALID_RESPONSE.copy(id = null)

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN address is null`() {
        val response = VALID_RESPONSE.copy(swapperAddress = null)

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN provider is null`() {
        val randomProvider = peraFixture<SwapQuoteProvider>().copy(name = "invalid-provider")

        val result = sut(VALID_RESPONSE, listOf(randomProvider))

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset in detail is null`() {
        every { assetDetailMapper(response = ASSET_IN_DETAIL_RESPONSE) } returns null
        val response = VALID_RESPONSE.copy(assetInAssetDetailResponse = ASSET_IN_DETAIL_RESPONSE)

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset out detail is null`() {
        every { assetDetailMapper(response = ASSET_OUT_DETAIL_RESPONSE) } returns null
        val response = VALID_RESPONSE.copy(assetOutAssetDetailResponse = ASSET_OUT_DETAIL_RESPONSE)

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset in amount is null`() {
        every { assetAmountMapper.mapAssetInAmount(response = VALID_RESPONSE) } returns null

        val result = sut(VALID_RESPONSE, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset out amount is null`() {
        every { assetAmountMapper.mapAssetOutAmount(response = VALID_RESPONSE) } returns null

        val result = sut(VALID_RESPONSE, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN price is null`() {
        val response = VALID_RESPONSE.copy(price = null)
        every { assetAmountMapper.mapAssetInAmount(response) } returns ASSET_IN_AMOUNT
        every { assetAmountMapper.mapAssetOutAmount(response) } returns ASSET_OUT_AMOUNT

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN price can not be parsed`() {
        val response = VALID_RESPONSE.copy(price = "invalid-price")
        every { assetAmountMapper.mapAssetInAmount(response) } returns ASSET_IN_AMOUNT
        every { assetAmountMapper.mapAssetOutAmount(response) } returns ASSET_OUT_AMOUNT

        val result = sut(response, QUOTE_PROVIDERS)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are null`() {
        val response = VALID_RESPONSE.copy(
            priceImpact = null,
            slippage = null,
            peraFeeAmount = null,
            exchangeFeeAmount = null
        )
        every { assetAmountMapper.mapAssetInAmount(response) } returns ASSET_IN_AMOUNT
        every { assetAmountMapper.mapAssetOutAmount(response) } returns ASSET_OUT_AMOUNT

        val result = sut(response, QUOTE_PROVIDERS)

        val expected = VALID_QUOTE.copy(
            priceImpact = 0f,
            slippage = 0f,
            fee = SwapQuoteV2.SwapFee(
                peraFeeAmount = BigDecimal.ZERO,
                exchangeFeeAmount = BigDecimal.ZERO,
                totalFee = BigDecimal.ZERO
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT default values WHEN price impact and slippage can not be parsed`() {
        val response = VALID_RESPONSE.copy(
            priceImpact = "invalid-impact",
            slippage = "invalid-slippage"
        )
        every { assetAmountMapper.mapAssetInAmount(response) } returns ASSET_IN_AMOUNT
        every { assetAmountMapper.mapAssetOutAmount(response) } returns ASSET_OUT_AMOUNT

        val result = sut(response, QUOTE_PROVIDERS)

        val expected = VALID_QUOTE.copy(priceImpact = 0f, slippage = 0f)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mapped swap quote`() {
        val result = sut(VALID_RESPONSE, listOf(QUOTE_PROVIDER))

        assertEquals(VALID_QUOTE, result)
    }

    private companion object {

        val ASSET_IN_DETAIL_RESPONSE = peraFixture<SwapQuoteAssetDetailResponse>()
        val ASSET_OUT_DETAIL_RESPONSE = peraFixture<SwapQuoteAssetDetailResponse>()
        val ASSET_IN_DETAIL = peraFixture<SwapQuoteV2.AssetDetail>()
        val ASSET_OUT_DETAIL = peraFixture<SwapQuoteV2.AssetDetail>()

        val QUOTE_PROVIDER_RESPONSE = peraFixture<String>()
        val QUOTE_PROVIDER = peraFixture<SwapQuoteProvider>().copy(name = QUOTE_PROVIDER_RESPONSE)
        val QUOTE_PROVIDERS = listOf(QUOTE_PROVIDER)

        val ASSET_IN_AMOUNT = peraFixture<SwapQuoteV2.AssetAmount>()
        val ASSET_OUT_AMOUNT = peraFixture<SwapQuoteV2.AssetAmount>()

        val VALID_RESPONSE = SwapQuoteResponse(
            id = 123L,
            provider = QUOTE_PROVIDER_RESPONSE,
            swapType = SwapTypeResponse.FIXED_INPUT,
            swapperAddress = "test-address",
            deviceId = 1234L,
            assetInAssetDetailResponse = ASSET_IN_DETAIL_RESPONSE,
            assetOutAssetDetailResponse = ASSET_OUT_DETAIL_RESPONSE,
            assetInAmount = BigInteger.ONE,
            assetInAmountWithSlippage = BigInteger.ONE,
            assetInAmountInUsdValue = BigDecimal.ONE,
            assetOutAmount = BigInteger.TEN,
            assetOutAmountWithSlippage = BigInteger.TEN,
            assetOutAmountInUsdValue = BigDecimal.TEN,
            slippage = "0.01",
            price = "1.0",
            priceImpact = "0.05",
            peraFeeAmount = BigInteger.valueOf(1000),
            exchangeFeeAmount = BigInteger.valueOf(2000)
        )

        val VALID_QUOTE = SwapQuoteV2(
            quoteId = 123L,
            provider = QUOTE_PROVIDER,
            swapType = SwapType.FIXED_INPUT,
            accountAddress = "test-address",
            assetInDetail = ASSET_IN_DETAIL,
            assetOutDetail = ASSET_OUT_DETAIL,
            assetInAmount = ASSET_IN_AMOUNT,
            assetOutAmount = ASSET_OUT_AMOUNT,
            price = 1.0f,
            priceImpact = 0.05f,
            slippage = 0.01f,
            fee = SwapQuoteV2.SwapFee(
                peraFeeAmount = BigDecimal("0.001000"),
                exchangeFeeAmount = BigDecimal("0.002000"),
                totalFee = BigDecimal("0.003000")
            )
        )
    }
}
