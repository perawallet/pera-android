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
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSwapQuoteProviderMapperTest {

    private val sut = DefaultSwapQuoteProviderMapper()

    @Test
    fun `EXPECT TINYMAN WHEN response is TINYMAN`() {
        val result = sut(SwapQuoteProviderResponse.TINYMAN)

        assertEquals(SwapQuoteProvider.TINYMAN, result)
    }

    @Test
    fun `EXPECT TINYMAN_V2 WHEN response is TINYMAN_V2`() {
        val result = sut(SwapQuoteProviderResponse.TINYMAN_V2)

        assertEquals(SwapQuoteProvider.TINYMAN_V2, result)
    }

    @Test
    fun `EXPECT TINYMAN_SWAP_ROUTER WHEN response is TINYMAN_SWAP_ROUTER`() {
        val result = sut(SwapQuoteProviderResponse.TINYMAN_SWAP_ROUTER)

        assertEquals(SwapQuoteProvider.TINYMAN_SWAP_ROUTER, result)
    }

    @Test
    fun `EXPECT VESTIGE_V3 WHEN response is VESTIGE_V3`() {
        val result = sut(SwapQuoteProviderResponse.VESTIGE_V3)

        assertEquals(SwapQuoteProvider.VESTIGE_V3, result)
    }

    @Test
    fun `EXPECT VESTIGE_V4 WHEN response is VESTIGE_V4`() {
        val result = sut(SwapQuoteProviderResponse.VESTIGE_V4)

        assertEquals(SwapQuoteProvider.VESTIGE_V4, result)
    }

    @Test
    fun `EXPECT FOLKS WHEN response is FOLKS_ROUTER`() {
        val result = sut(SwapQuoteProviderResponse.FOLKS_ROUTER)

        assertEquals(SwapQuoteProvider.FOLKS, result)
    }

    @Test
    fun `EXPECT DEFLEX WHEN response is DEFLEX`() {
        val result = sut(SwapQuoteProviderResponse.DEFLEX)

        assertEquals(SwapQuoteProvider.DEFLEX, result)
    }

    @Test
    fun `EXPECT null WHEN response is UNKNOWN`() {
        val result = sut(SwapQuoteProviderResponse.UNKNOWN)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN response is null`() {
        val result = sut(null)

        assertNull(result)
    }
}
