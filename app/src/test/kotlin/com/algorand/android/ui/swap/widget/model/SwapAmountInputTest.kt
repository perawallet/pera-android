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

package com.algorand.android.ui.swap.widget.model

import com.algorand.android.ui.swap.widget.model.SwapAmountInput.Input
import com.algorand.test.test
import java.math.BigDecimal
import java.util.Locale
import org.junit.Test

class SwapAmountInputTest {

    val sut = SwapAmountInput(Locale.ENGLISH)
    val amountObserver = sut.amountInputFlow.test()

    @Test
    fun `EXPECT no update when inputs are invalid`() {
        val asciiStart = 32
        val asciiEnd = 127
        var index = asciiStart
        while (index <= asciiEnd) {
            sut.setInput(index.toChar().toString())
            index += 1
        }

        // Comma is being replaced with dot.
        // Second valid input is comma (ASCII-44) and third valid one is dot (ASCII-46).
        // Since previous value is also dot (comma being replaced), flow is not being updated,
        // That's why there is only one dot in the history.
        amountObserver.assertValueHistory(
            Input("", null),
            Input(".", null),
            Input("0", BigDecimal.valueOf(0)),
            Input("1", BigDecimal.valueOf(1)),
            Input("2", BigDecimal.valueOf(2)),
            Input("3", BigDecimal.valueOf(3)),
            Input("4", BigDecimal.valueOf(4)),
            Input("5", BigDecimal.valueOf(5)),
            Input("6", BigDecimal.valueOf(6)),
            Input("7", BigDecimal.valueOf(7)),
            Input("8", BigDecimal.valueOf(8)),
            Input("9", BigDecimal.valueOf(9))
        )
    }

    @Test
    fun `EXPECT decimal numbers WHEN input starts with separator`() {
        sut.setInput("1")
        sut.setInput("12")
        sut.setInput("123")
        sut.setInput("1234")
        sut.setInput("123")
        sut.setInput("12")
        sut.setInput("1")
        sut.setInput("")
        sut.setInput(".")
        sut.setInput(".1")
        sut.setInput(".")
        sut.setInput("")
        sut.setInput("0")
        sut.setInput("0.")
        sut.setInput("0.1")
        sut.setInput("0.12")
        sut.setInput("0.123")

        amountObserver.assertValueHistory(
            Input("", null),
            Input("1", BigDecimal.valueOf(1)),
            Input("12", BigDecimal.valueOf(12)),
            Input("123", BigDecimal.valueOf(123)),
            Input("1234", BigDecimal.valueOf(1234)),
            Input("123", BigDecimal.valueOf(123)),
            Input("12", BigDecimal.valueOf(12)),
            Input("1", BigDecimal.valueOf(1)),
            Input("", null),
            Input(".", null),
            Input(".1", BigDecimal("0.1")),
            Input(".", null),
            Input("", null),
            Input("0", BigDecimal.valueOf(0)),
            Input("0.", BigDecimal.valueOf(0)),
            Input("0.1", BigDecimal("0.1")),
            Input("0.12", BigDecimal("0.12")),
            Input("0.123", BigDecimal("0.123"))
        )
    }

    @Test
    fun `EXPECT different locales to be supported`() {
        val sut = SwapAmountInput(Locale.GERMAN)
        val amountObserver = sut.amountInputFlow.test()

        sut.setInput(".")
        sut.setInput(".1")
        sut.setInput(".")
        sut.setInput("")
        sut.setInput("0")
        sut.setInput("0.")
        sut.setInput("0.1")

        amountObserver.assertValueHistory(
            Input("", null),
            Input(",", null),
            Input(",1", BigDecimal("0.1")),
            Input(",", null),
            Input("", null),
            Input("0", BigDecimal.valueOf(0)),
            Input("0,", BigDecimal.valueOf(0)),
            Input("0,1", BigDecimal("0.1"))
        )
    }
}
