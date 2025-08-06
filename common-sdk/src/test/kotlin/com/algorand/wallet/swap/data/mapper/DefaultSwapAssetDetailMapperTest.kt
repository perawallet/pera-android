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

import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.data.model.VerificationTierResponse
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.swap.data.model.SwapQuoteAssetDetailResponse
import com.algorand.wallet.swap.domain.model.SwapQuote
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSwapAssetDetailMapperTest {

    private val verificationTierMapper: VerificationTierMapper = mockk {
        every { invoke(response = VerificationTierResponse.VERIFIED) } returns VerificationTier.VERIFIED
    }

    private val sut = DefaultSwapAssetDetailMapper(verificationTierMapper)

    @Test
    fun `EXPECT null WHEN response is null`() {
        val result = sut(null)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN assetId is null`() {
        val response = VALID_ASSET_DETAIL_RESPONSE.copy(assetId = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN fractionDecimals is null`() {
        val response = VALID_ASSET_DETAIL_RESPONSE.copy(fractionDecimals = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT zero WHEN usdValue is null`() {
        val response = VALID_ASSET_DETAIL_RESPONSE.copy(usdValue = null)

        val result = sut(response)

        assertEquals(BigDecimal.ZERO, result?.usdValue)
    }

    @Test
    fun `EXPECT mapped asset detail WHEN response is valid`() {
        val result = sut(VALID_ASSET_DETAIL_RESPONSE)

        assertEquals(VALID_ASSET_DETAIL, result)
    }

    @Test
    fun `EXPECT normalized asset id WHEN asset is Algo`() {
        val response = VALID_ASSET_DETAIL_RESPONSE.copy(assetId = 0)

        val result = sut(response)

        assertEquals(-7L, result?.assetId)
    }

    private companion object {
        val VALID_ASSET_DETAIL_RESPONSE = SwapQuoteAssetDetailResponse(
            assetId = 12345,
            logoUrl = "https://example.com/logo.png",
            name = "Test Asset",
            shortName = "TST",
            total = BigInteger.TEN,
            fractionDecimals = 2,
            verificationTierResponse = VerificationTierResponse.VERIFIED,
            usdValue = BigDecimal.ONE
        )

        val VALID_ASSET_DETAIL = SwapQuote.AssetDetail(
            assetId = 12345,
            logoUrl = "https://example.com/logo.png",
            name = "Test Asset",
            shortName = "TST",
            total = BigInteger.TEN,
            fractionDecimals = 2,
            verificationTier = VerificationTier.VERIFIED,
            usdValue = BigDecimal.ONE
        )
    }
}
