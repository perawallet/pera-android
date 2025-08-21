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
    fun `EXPECT null WHEN name is null`() {
        val response = VALID_RESPONSE.copy(name = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN displayName is null`() {
        val response = VALID_RESPONSE.copy(displayName = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped provider WHEN response is valid`() {
        val result = sut(VALID_RESPONSE)

        assertEquals(VALID_PROVIDER, result)
    }

    private companion object {

        val VALID_RESPONSE = SwapQuoteProviderResponse(
            name = "example",
            displayName = "Example",
            iconUrl = "https://example.com/icon.png"
        )

        val VALID_PROVIDER = SwapQuoteProvider(
            name = "example",
            displayName = "Example",
            iconUrl = "https://example.com/icon.png"
        )
    }
}
