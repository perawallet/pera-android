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

import com.algorand.wallet.swap.data.model.SwapQuoteRequestBody
import com.algorand.wallet.swap.data.model.SwapTypeResponse
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSwapQuoteRequestBodyMapperTest {

    private val sut = DefaultSwapQuoteRequestBodyMapper()

    @Test
    fun `EXPECT mapped request body`() {
        val result = sut(PAYLOAD)

        assertEquals(REQUEST_BODY, result)
    }

    private companion object {
        val PAYLOAD = SwapQuoteRequestPayload(
            address = "address",
            deviceId = "deviceId",
            assetInId = 1L,
            assetOutId = 2L,
            amount = BigInteger.valueOf(10_000),
            slippage = 0.01
        )

        val REQUEST_BODY = SwapQuoteRequestBody(
            swapperAddress = "address",
            deviceId = "deviceId",
            assetInId = 1L,
            assetOutId = 2L,
            amount = BigInteger.valueOf(10_000),
            slippage = 0.01,
            swapType = SwapTypeResponse.FIXED_INPUT
        )
    }
}
