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

package com.algorand.wallet.encryption.data.manager

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class Base64ManagerImplTest {

    private lateinit var sut: Base64ManagerImpl

    @Before
    fun setup() {
        sut = Base64ManagerImpl()
    }

    @Test
    fun `GIVEN byte array WHEN encode is called THEN return base64 encoded string`() {
        val input = "Hello, World!".toByteArray()
        val expected = "SGVsbG8sIFdvcmxkIQ=="

        val result = sut.encode(input)

        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN base64 encoded string WHEN decode is called THEN return original byte array`() {
        val input = "SGVsbG8sIFdvcmxkIQ=="
        val expected = "Hello, World!".toByteArray()

        val result = sut.decode(input)

        assertArrayEquals(expected, result)
    }

    @Test
    fun `GIVEN base64 encoded string and flags WHEN decode with flags is called THEN return original byte array`() {
        val input = "SGVsbG8sIFdvcmxkIQ=="
        val flags = 0
        val expected = "Hello, World!".toByteArray()

        val result = sut.decode(input, flags)

        assertArrayEquals(expected, result)
    }

    @Test
    fun `GIVEN empty byte array WHEN encode is called THEN return empty string`() {
        val input = ByteArray(0)

        val result = sut.encode(input)

        assertEquals("", result)
    }

    @Test
    fun `GIVEN empty string WHEN decode is called THEN return empty byte array`() {
        val input = ""

        val result = sut.decode(input)

        assertArrayEquals(ByteArray(0), result)
    }
}
