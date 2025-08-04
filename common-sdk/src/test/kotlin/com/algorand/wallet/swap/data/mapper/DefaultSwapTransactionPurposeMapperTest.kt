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

import com.algorand.wallet.swap.data.model.SwapTransactionPurposeResponse
import com.algorand.wallet.swap.domain.model.SwapTransactionPurpose
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSwapTransactionPurposeMapperTest {

    private val sut = DefaultSwapTransactionPurposeMapper()

    @Test
    fun `EXPECT OPT_IN WHEN response is OPT_IN`() {
        val result = sut(SwapTransactionPurposeResponse.OPT_IN)

        assertEquals(SwapTransactionPurpose.OPT_IN, result)
    }

    @Test
    fun `EXPECT SWAP WHEN response is SWAP`() {
        val result = sut(SwapTransactionPurposeResponse.SWAP)

        assertEquals(SwapTransactionPurpose.SWAP, result)
    }

    @Test
    fun `EXPECT PERA_FEE WHEN response is PERA_FEE`() {
        val result = sut(SwapTransactionPurposeResponse.PERA_FEE)

        assertEquals(SwapTransactionPurpose.PERA_FEE, result)
    }

    @Test
    fun `EXPECT UNKNOWN WHEN response is UNKNOWN`() {
        val result = sut(SwapTransactionPurposeResponse.UNKNOWN)

        assertEquals(SwapTransactionPurpose.UNKNOWN, result)
    }

    @Test
    fun `EXPECT UNKNOWN WHEN response is null`() {
        val result = sut(null)

        assertEquals(SwapTransactionPurpose.UNKNOWN, result)
    }
}
