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

package com.algorand.wallet.wealth.address.data.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.utils.date.parser.DateTimeParser
import com.algorand.wallet.wealth.address.domain.model.AddressWealth
import com.algorand.wallet.wealth.address.domain.model.AddressWealthChartData
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResult
import com.algorand.wallet.wealth.wallet.data.model.WalletChartResponseResults
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.OffsetDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultAddressWealthMapperTest {

    private val dateTimeParser: DateTimeParser = mockk()

    private val sut = DefaultAddressWealthMapper(dateTimeParser)

    @Test
    fun `EXPECT empty chart data WHEN result is null`() {
        val result = sut.map(WalletChartResponseResults(null))

        val expected = AddressWealth(emptyList())
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT invalid data to be filtered WHEN result has invalid data`() {
        every { dateTimeParser.parseOffsetDateTime(DATE_TIME_RESPONSE) } returns OFFSET_DATETIME
        every { dateTimeParser.parseOffsetDateTime("invalid-datetime") } returns null
        every { dateTimeParser.parseOffsetDateTime("") } returns null
        val results = listOf(
            VALID_CHART_DATA_RESPONSE,
            INVALID_DATETIME_RESPONSE,
            NULL_DATETIME_RESPONSE,
            INVALID_USD_VALUE_RESPONSE,
            NULL_USD_VALUE_RESPONSE,
            INVALID_ALGO_VALUE_RESPONSE,
            NULL_ALGO_VALUE_RESPONSE,
            INVALID_ROUND_RESPONSE
        )

        val result = sut.map(WalletChartResponseResults(results))

        val expected = AddressWealth(listOf(VALID_CHART_DATA))
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT chart data to be sorted by round WHEN result is unsorted`() {
        every { dateTimeParser.parseOffsetDateTime(DATE_TIME_RESPONSE) } returns OFFSET_DATETIME
        val results = listOf(
            VALID_CHART_DATA_RESPONSE.copy(round = 2),
            VALID_CHART_DATA_RESPONSE.copy(round = 3),
            VALID_CHART_DATA_RESPONSE.copy(round = 1)
        )

        val result = sut.map(WalletChartResponseResults(results))

        val expected = AddressWealth(
            listOf(
                VALID_CHART_DATA.copy(round = 1),
                VALID_CHART_DATA.copy(round = 2),
                VALID_CHART_DATA.copy(round = 3)
            )
        )
        assertEquals(expected, result)
    }

    private companion object {
        const val DATE_TIME_RESPONSE = "2023-10-01T00:00:00Z"
        val VALID_CHART_DATA_RESPONSE = WalletChartResponseResult(
            datetime = DATE_TIME_RESPONSE,
            usdValue = "1000.12",
            algoValue = "100.23",
            round = 1234567
        )

        val INVALID_DATETIME_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(datetime = "invalid-datetime")
        val NULL_DATETIME_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(datetime = null)
        val INVALID_USD_VALUE_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(usdValue = "invalid-value")
        val NULL_USD_VALUE_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(usdValue = null)
        val INVALID_ALGO_VALUE_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(algoValue = "invalid-value")
        val NULL_ALGO_VALUE_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(algoValue = null)
        val INVALID_ROUND_RESPONSE = VALID_CHART_DATA_RESPONSE.copy(round = null)

        val OFFSET_DATETIME: OffsetDateTime = peraFixture()
        val VALID_CHART_DATA = AddressWealthChartData(
            datetime = OFFSET_DATETIME,
            usdValue = BigDecimal.valueOf(1000.12),
            algoValue = BigDecimal.valueOf(100.23),
            round = 1234567
        )
    }
}
