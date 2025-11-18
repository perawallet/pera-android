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

package com.algorand.wallet.wealth.wallet.data.mapper

import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultWalletWealthPeriodRequestMapperTest {

    private val sut = DefaultWalletWealthPeriodRequestMapper()

    @Test
    fun `EXPECT one-day WHEN period is ONE_DAY`() {
        val result = sut.invoke(WalletWealthPeriod.ONE_DAY)

        val expected = "one-day"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT one-week WHEN period is ONE_WEEK`() {
        val result = sut.invoke(WalletWealthPeriod.ONE_WEEK)

        val expected = "one-week"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT one-month WHEN period is ONE_MONTH`() {
        val result = sut.invoke(WalletWealthPeriod.ONE_MONTH)

        val expected = "one-month"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT one-year WHEN period is ONE_YEAR`() {
        val result = sut.invoke(WalletWealthPeriod.ONE_YEAR)

        val expected = "one-year"
        assertEquals(expected, result)
    }
}
