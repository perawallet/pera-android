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

package com.algorand.wallet.asset.pricehistory.data.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.asset.pricehistory.data.model.AssetPriceHistoryResponse
import com.algorand.wallet.asset.pricehistory.domain.model.AssetPriceHistory
import com.algorand.wallet.utils.date.parser.DateTimeParser
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.OffsetDateTime

class DefaultAssetPriceHistoryMapperTest {

    private val dateTimeParser: DateTimeParser = mockk {
        every { parseOffsetDateTime(DATETIME_RESPONSE) } returns OFFSET_DATE_TIME
    }

    private val sut = DefaultAssetPriceHistoryMapper(dateTimeParser)

    @Test
    fun `EXPECT null WHEN date time response is null`() {
        val response = VALID_RESPONSE.copy(datetime = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN parsed date time is null`() {
        every { dateTimeParser.parseOffsetDateTime(DATETIME_RESPONSE) } returns null

        val result = sut(VALID_RESPONSE)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN price is null`() {
        val response = VALID_RESPONSE.copy(price = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN price can not be converted to BigDecimal`() {
        val response = VALID_RESPONSE.copy(price = "invalid_price")

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT asset price history WHEN all fields are valid`() {
        val result = sut(VALID_RESPONSE)

        val expected = AssetPriceHistory(OFFSET_DATE_TIME, BigDecimal.valueOf(1.2345))
        assertEquals(expected, result)
    }

    private companion object {
        const val DATETIME_RESPONSE = "2023-10-01T00:00:00Z"
        const val PRICE = "1.2345"
        val OFFSET_DATE_TIME = peraFixture<OffsetDateTime>()

        val VALID_RESPONSE = AssetPriceHistoryResponse(
            datetime = DATETIME_RESPONSE,
            price = PRICE
        )
    }
}
