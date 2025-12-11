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

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class AmountRendererTest {

    private val formattedAmount: FormattedAmount = mockk {
        every { getFormattedValue() } returns FORMATTED_AMOUNT
    }

    @Test
    fun `EXPECT formatted amount WHEN prefix is null`() {
        val sut = AmountRenderer(formattedAmount, AmountRenderer.RenderType.Plain, prefix = null)

        val result = sut.getDisplayValue()

        assertEquals(FORMATTED_AMOUNT, result)
    }

    @Test
    fun `EXPECT formatted amount WHEN prefix is blank`() {
        val sut = AmountRenderer(formattedAmount, AmountRenderer.RenderType.Plain, prefix = " ")

        val result = sut.getDisplayValue()

        assertEquals(FORMATTED_AMOUNT, result)
    }

    @Test
    fun `EXPECT formatted amount with prefix WHEN prefix is not blank`() {
        val sut = AmountRenderer(formattedAmount, AmountRenderer.RenderType.Plain, prefix = "$")

        val result = sut.getDisplayValue()

        assertEquals("$$FORMATTED_AMOUNT", result)
    }

    @Test
    fun `EXPECT hidden pattern WHEN render type is hidden`() {
        val sut = AmountRenderer(formattedAmount, AmountRenderer.RenderType.Hidden(), prefix = null)

        val result = sut.getDisplayValue()

        assertEquals("****", result)
    }

    @Test
    fun `EXPECT formatted amount WHEN suffix is null`() {
        val sut = AmountRenderer(
            formattedAmount,
            AmountRenderer.RenderType.Plain,
            suffix = null
        )

        val result = sut.getDisplayValue()

        assertEquals(FORMATTED_AMOUNT, result)
    }

    @Test
    fun `EXPECT formatted amount WHEN suffix is blank`() {
        val sut = AmountRenderer(
            formattedAmount,
            AmountRenderer.RenderType.Plain,
            suffix = " "
        )

        val result = sut.getDisplayValue()

        assertEquals(FORMATTED_AMOUNT, result)
    }

    @Test
    fun `EXPECT formatted amount with suffix WHEN suffix is not blank`() {
        val sut = AmountRenderer(
            formattedAmount,
            AmountRenderer.RenderType.Plain,
            suffix = "ALGO"
        )

        val result = sut.getDisplayValue()

        assertEquals("$FORMATTED_AMOUNT ALGO", result)
    }

    private companion object {
        const val FORMATTED_AMOUNT = "100.00"
    }
}
