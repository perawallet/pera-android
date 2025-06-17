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

class PatternFormattedAmountTest {

    @Test
    fun `EXPECT minimum 2 decimals WHEN pattern is Algo`() {
        val amount = PeraAmount(1_231_141_500_000, 6)
        val patternType = PatternFormattedAmount.PatternType.Algo

        val result = PatternFormattedAmount(amount, patternType).getFormattedValue()

        val expected = "1,231,141.50"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT max 6 decimals WHEN pattern is Algo`() {
        val amount = PeraAmount(BigDecimal.valueOf(1_231_141.52112421))
        val patternType = PatternFormattedAmount.PatternType.Algo

        val result = PatternFormattedAmount(amount, patternType).getFormattedValue()

        val expected = "1,231,141.521124"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT custom pattern with default zeros WHEN number is less than one`() {
        val pattern = "###,#00.0000"
        val amount = PeraAmount(BigDecimal.valueOf(0.5))
        val patternType = PatternFormattedAmount.PatternType.Custom(pattern)

        val result = PatternFormattedAmount(amount, patternType).getFormattedValue()

        val expected = "00.5000"
        assertEquals(expected, result)
    }
}
