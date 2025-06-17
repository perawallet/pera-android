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

package com.algorand.android.ui.common.amount

import java.math.BigDecimal
import kotlin.test.assertEquals
import org.junit.Test

class CompactFormattedAmountTest {

    @Test
    fun `EXPECT 2 decimals WHEN type is fiat and number is more than one`() {
        val amount = PeraAmount(BigDecimal.valueOf(122113.421566121))
        val fractionalType = CompactFormattedAmount.FractionalType.Fiat

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "122,113.42"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT 6 decimals WHEN type is fiat and number is smaller than one`() {
        val amount = PeraAmount(BigDecimal.valueOf(0.123456789))
        val fractionalType = CompactFormattedAmount.FractionalType.Fiat

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "0.123456"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT 2 decimals WHEN type is asset and number is bigger than ten`() {
        val amount = PeraAmount(BigDecimal.valueOf(122113.421566121))
        val fractionalType = CompactFormattedAmount.FractionalType.Asset

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "122,113.42"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT 4 decimals WHEN type is asset and number is smaller than ten and bigger than one`() {
        val amount = PeraAmount(BigDecimal.valueOf(9.123456789))
        val fractionalType = CompactFormattedAmount.FractionalType.Asset

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "9.1234"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT 6 decimals WHEN type is asset and number is smaller than one`() {
        val amount = PeraAmount(BigDecimal.valueOf(0.123456789))
        val fractionalType = CompactFormattedAmount.FractionalType.Asset

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "0.123456"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT compact amount without decimal WHEN type is collectible`() {
        val amount = PeraAmount(BigDecimal.valueOf(123451.14151))
        val fractionalType = CompactFormattedAmount.FractionalType.Collectible

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "123.4K"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT 1 decimal WHEN type is collectible and number is smaller than one`() {
        val amount = PeraAmount(BigDecimal.valueOf(0.56789))
        val fractionalType = CompactFormattedAmount.FractionalType.Collectible

        val result = CompactFormattedAmount(amount, fractionalType).getFormattedValue()

        val expected = "0.5"
        assertEquals(expected, result)
    }
}
