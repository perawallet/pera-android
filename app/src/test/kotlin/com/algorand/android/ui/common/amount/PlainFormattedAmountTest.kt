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

class PlainFormattedAmountTest {

    @Test
    fun `EXPECT max 6 decimals WHEN type is Algo without decimal config`() {
        val amount = PeraAmount(BigDecimal.valueOf(123456789.1234567))

        val result = PlainFormattedAmount.AlgoPlainFormattedAmount(amount).getFormattedValue()

        val expected = "123,456,789.123456"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT custom decimal WHEN type is Algo with decimal config`() {
        val amount = PeraAmount(BigDecimal.valueOf(123456789.12))
        val decimalConfig = DecimalConfig(4, DecimalConfig.MinDecimalType.Fixed(3))

        val result = PlainFormattedAmount.AlgoPlainFormattedAmount(amount, decimalConfig).getFormattedValue()

        val expected = "123,456,789.120"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT formatted amount with given decimal config WHEN type is Fiat`() {
        val amount = PeraAmount(BigDecimal.valueOf(123456789.1234567))
        val decimalConfig = DecimalConfig(2, DecimalConfig.MinDecimalType.FixedToMax)

        val result = PlainFormattedAmount.FiatPlainFormattedAmount(amount, decimalConfig).getFormattedValue()

        val expected = "123,456,789.12"
        assertEquals(expected, result)
    }
}
