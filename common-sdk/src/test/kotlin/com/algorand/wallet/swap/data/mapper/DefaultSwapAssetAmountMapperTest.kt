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
import com.algorand.wallet.swap.domain.model.SwapQuote
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSwapAssetAmountMapperTest {

    private val sut = DefaultSwapAssetAmountMapper()

    @Test
    fun `EXPECT null WHEN asset in decimals is null`() {
        val response = VALID_RESPONSE.copy(
            assetInAssetDetailResponse = VALID_RESPONSE.assetInAssetDetailResponse?.copy(
                fractionDecimals = null
            )
        )

        val result = sut.mapAssetInAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset in detail is null`() {
        val response = VALID_RESPONSE.copy(assetInAssetDetailResponse = null)

        val result = sut.mapAssetInAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset in amount is null`() {
        val response = VALID_RESPONSE.copy(
            assetInAmount = null
        )

        val result = sut.mapAssetInAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT zero as default value WHEN asset in amount in USD value is null`() {
        val response = VALID_RESPONSE.copy(
            assetInAmountInUsdValue = null
        )

        val result = sut.mapAssetInAmount(response)

        assertEquals(BigDecimal.ZERO, result?.amountInUsdValue)
    }

    @Test
    fun `EXPECT null WHEN asset in amount with slippage is null`() {
        val response = VALID_RESPONSE.copy(
            assetInAmountWithSlippage = null
        )

        val result = sut.mapAssetInAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped asset in amount WHEN all values are valid`() {
        val result = sut.mapAssetInAmount(VALID_RESPONSE)

        assertEquals(VALID_ASSET_IN_AMOUNT, result)
    }

    @Test
    fun `EXPECT null WHEN asset out decimals is null`() {
        val response = VALID_RESPONSE.copy(
            assetOutAssetDetailResponse = VALID_RESPONSE.assetOutAssetDetailResponse?.copy(
                fractionDecimals = null
            )
        )

        val result = sut.mapAssetOutAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset out detail is null`() {
        val response = VALID_RESPONSE.copy(assetOutAssetDetailResponse = null)

        val result = sut.mapAssetOutAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN asset out amount is null`() {
        val response = VALID_RESPONSE.copy(
            assetOutAmount = null
        )

        val result = sut.mapAssetOutAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT zero as default value WHEN asset out amount in USD value is null`() {
        val response = VALID_RESPONSE.copy(
            assetOutAmountInUsdValue = null
        )

        val result = sut.mapAssetOutAmount(response)

        assertEquals(BigDecimal.ZERO, result?.amountInUsdValue)
    }

    @Test
    fun `EXPECT null WHEN asset out amount with slippage is null`() {
        val response = VALID_RESPONSE.copy(
            assetOutAmountWithSlippage = null
        )

        val result = sut.mapAssetOutAmount(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped asset out amount WHEN all values are valid`() {
        val result = sut.mapAssetOutAmount(VALID_RESPONSE)

        assertEquals(VALID_ASSET_OUT_AMOUNT, result)
    }

    private companion object {
        val VALID_RESPONSE = peraFixture<SwapQuoteResponse>().copy(
            assetInAssetDetailResponse = peraFixture<SwapQuoteAssetDetailResponse>().copy(
                fractionDecimals = 6
            ),
            assetInAmount = BigInteger.ONE,
            assetInAmountInUsdValue = BigDecimal.TEN,
            assetInAmountWithSlippage = BigInteger.TEN,
            assetOutAssetDetailResponse = peraFixture<SwapQuoteAssetDetailResponse>().copy(
                fractionDecimals = 6
            ),
            assetOutAmount = BigInteger.ONE,
            assetOutAmountInUsdValue = BigDecimal.TEN,
            assetOutAmountWithSlippage = BigInteger.TEN
        )

        val VALID_ASSET_IN_AMOUNT = SwapQuote.AssetAmount(
            amount = BigDecimal("0.000001"),
            amountInUsdValue = BigDecimal.TEN,
            amountWithSlippage = BigDecimal("0.000010")
        )

        val VALID_ASSET_OUT_AMOUNT = SwapQuote.AssetAmount(
            amount = BigDecimal("0.000001"),
            amountInUsdValue = BigDecimal.TEN,
            amountWithSlippage = BigDecimal("0.000010")
        )
    }
}
