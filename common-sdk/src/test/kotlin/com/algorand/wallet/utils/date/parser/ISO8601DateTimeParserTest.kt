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

package com.algorand.wallet.utils.date.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ISO8601DateTimeParserTest {

    private val sut = ISO8601DateTimeParser()

    @Test
    fun `EXPECT parsed datetime WHEN datetime is valid iso8601 value`() {
        val input = "2024-06-24T09:44:58+03:00"

        val result = sut.parseOffsetDateTime(input)

        assertNotNull(result)
        assertEquals(2024, result?.year)
        assertEquals(6, result?.monthValue)
        assertEquals(24, result?.dayOfMonth)
        assertEquals(9, result?.hour)
        assertEquals(44, result?.minute)
        assertEquals(58, result?.second)
        assertEquals("+03:00", result?.offset.toString())
    }

    @Test
    fun `EXPECT parsed datetime WHEN datetime is valid iso8601 value without column in timezone`() {
        val input = "2024-06-24T09:44:58+0300"

        val result = sut.parseOffsetDateTime(input)

        assertNotNull(result)
        assertEquals(2024, result?.year)
        assertEquals(6, result?.monthValue)
        assertEquals(24, result?.dayOfMonth)
        assertEquals(9, result?.hour)
        assertEquals(44, result?.minute)
        assertEquals(58, result?.second)
        assertEquals("+03:00", result?.offset.toString())
    }

    @Test
    fun `EXPECT null WHEN datetime does not have offset`() {
        val input = "2024-06-24 09:44:58"

        val result = sut.parseOffsetDateTime(input)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN datetime is empty string`() {
        val result = sut.parseOffsetDateTime("")

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN datetime is completely invalid`() {
        val result = sut.parseOffsetDateTime("hello world")

        assertNull(result)
    }
}
