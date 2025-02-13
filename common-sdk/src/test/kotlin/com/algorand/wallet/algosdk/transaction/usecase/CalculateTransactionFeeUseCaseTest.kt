/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.algosdk.transaction.usecase

import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateTransactionFeeUseCaseTest {

    private val sut = CalculateTransactionFeeUseCase()

    @Test
    fun `EXPECT default min fee WHEN fee is less than default min fee and min fee is null`() {
        val result = sut(0, null, null)

        assertEquals(DEFAULT_MIN_FEE, result)
    }

    @Test
    fun `EXPECT default min fee WHEN fee is less than default min fee and min fee is 0`() {
        val result = sut(0, 0, null)

        assertEquals(DEFAULT_MIN_FEE, result)
    }

    @Test
    fun `EXPECT min fee WHEN fee is less than min fee and min fee is not null`() {
        val result = sut(0, 2000, null)

        assertEquals(BigInteger.valueOf(2000), result)
    }

    @Test
    fun `EXPECT calculated fee WHEN fee is greater than min fee and min fee is not null`() {
        val result = sut(1000, 1000, null)

        assertEquals(BigInteger.valueOf(270000), result)
    }

    @Test
    fun `EXPECT calculated fee WHEN fee is greater than min fee and min fee is null`() {
        val result = sut(1000, null, null)

        assertEquals(BigInteger.valueOf(270000), result)
    }

    @Test
    fun `EXPECT calculated fee WHEN fee is greater than min fee and min fee is null and signed txn is not null`() {
        val result = sut(1000, null, ByteArray(100))

        assertEquals(BigInteger.valueOf(100000), result)
    }

    private companion object {
        val DEFAULT_MIN_FEE: BigInteger = BigInteger.valueOf(1000L)
    }
}
