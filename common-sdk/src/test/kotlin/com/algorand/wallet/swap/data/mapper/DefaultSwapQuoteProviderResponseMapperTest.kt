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

import com.algorand.wallet.swap.data.model.SwapQuoteProviderResponse
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSwapQuoteProviderResponseMapperTest {

    private val sut = DefaultSwapQuoteProviderResponseMapper()

    @Test
    fun `EXPECT TINYMAN WHEN provider is TINYMAN`() {
        val result = sut(SwapQuoteProvider.TINYMAN)

        assertEquals(SwapQuoteProviderResponse.TINYMAN, result)
    }

    @Test
    fun `EXPECT TINYMAN_V2 WHEN provider is TINYMAN_V2`() {
        val result = sut(SwapQuoteProvider.TINYMAN_V2)

        assertEquals(SwapQuoteProviderResponse.TINYMAN_V2, result)
    }

    @Test
    fun `EXPECT TINYMAN_SWAP_ROUTER WHEN provider is TINYMAN_SWAP_ROUTER`() {
        val result = sut(SwapQuoteProvider.TINYMAN_SWAP_ROUTER)

        assertEquals(SwapQuoteProviderResponse.TINYMAN_SWAP_ROUTER, result)
    }

    @Test
    fun `EXPECT VESTIGE_V3 WHEN provider is VESTIGE_V3`() {
        val result = sut(SwapQuoteProvider.VESTIGE_V3)

        assertEquals(SwapQuoteProviderResponse.VESTIGE_V3, result)
    }

    @Test
    fun `EXPECT VESTIGE_V4 WHEN provider is VESTIGE_V4`() {
        val result = sut(SwapQuoteProvider.VESTIGE_V4)

        assertEquals(SwapQuoteProviderResponse.VESTIGE_V4, result)
    }

    @Test
    fun `EXPECT FOLKS_ROUTER WHEN provider is FOLKS`() {
        val result = sut(SwapQuoteProvider.FOLKS)

        assertEquals(SwapQuoteProviderResponse.FOLKS_ROUTER, result)
    }

    @Test
    fun `EXPECT DEFLEX WHEN provider is DEFLEX`() {
        val result = sut(SwapQuoteProvider.DEFLEX)

        assertEquals(SwapQuoteProviderResponse.DEFLEX, result)
    }
}
