/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.deeplink.parser

import com.algorand.common.deeplink.model.PeraUri
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PeraUriParserImplTest {

    private val sut = PeraUriParserImpl()

    @Test
    fun `EXPECT PeraUri with empty raw uri when uri is empty string`() {
        val result = sut.parseUri("")

        val expected = PeraUri(
            scheme = null,
            host = null,
            path = null,
            queryParams = emptyMap(),
            fragment = null,
            rawUri = ""
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT scheme host path queryParams and fragment WHEN uri has all components`() {
        val uri = "pera://host/path?query1=value1&query2=value2#fragment"

        val result = sut.parseUri(uri)

        assertEquals("pera", result.scheme)
        assertEquals("host", result.host)
        assertEquals("path", result.path)
        assertEquals(mapOf("query1" to "value1", "query2" to "value2"), result.queryParams)
        assertEquals("fragment", result.fragment)
    }

    @Test
    fun `EXPECT PeraUri with raw uri only WHEN uri has no host path queryParams and fragment`() {
        val uri = "pera:"

        val result = sut.parseUri(uri)

        val expected = PeraUri(
            scheme = null,
            host = null,
            path = null,
            queryParams = emptyMap(),
            fragment = null,
            rawUri = uri
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT scheme and host only WHEN uri has no path queryParams and fragment`() {
        val uri = "pera://host"

        val result = sut.parseUri(uri)

        assertEquals("pera", result.scheme)
        assertEquals("host", result.host)
        assertNull(result.path)
        assertEquals(emptyMap(), result.queryParams)
        assertNull(result.fragment)
    }

    @Test
    fun `EXPECT scheme host path only WHEN uri has no queryParams and fragment`() {
        val uri = "pera://host/path"

        val result = sut.parseUri(uri)

        assertEquals("pera", result.scheme)
        assertEquals("host", result.host)
        assertEquals("path", result.path)
        assertEquals(emptyMap(), result.queryParams)
        assertNull(result.fragment)
    }

    @Test
    fun `EXPECT scheme host path queryParams only WHEN uri has no fragment`() {
        val uri = "pera://host/path?query1=value1&query2=value2"

        val result = sut.parseUri(uri)

        assertEquals("pera", result.scheme)
        assertEquals("host", result.host)
        assertEquals("path", result.path)
        assertEquals(mapOf("query1" to "value1", "query2" to "value2"), result.queryParams)
        assertNull(result.fragment)
    }

    @Test
    fun `EXPECT scheme host fragment only WHEN uri has no path queryParams`() {
        val uri = "pera://host#fragment"

        val result = sut.parseUri(uri)

        assertEquals("pera", result.scheme)
        assertEquals("host", result.host)
        assertNull(result.path)
        assertEquals(emptyMap(), result.queryParams)
        assertEquals("fragment", result.fragment)
    }

    @Test
    fun `EXPECT PeraUri with raw uri only WHEN uri has no scheme`() {
        val uri = "host/path?query1=value1&query2=value2#fragment"

        val result = sut.parseUri(uri)

        val expected = PeraUri(
            scheme = null,
            host = null,
            path = null,
            queryParams = emptyMap(),
            fragment = null,
            rawUri = uri
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT PeraUri with raw uri only WHEN uri has no host`() {
        val uri = "pera:/path?query1=value1&query2=value2#fragment"

        val result = sut.parseUri(uri)

        val expected = PeraUri(
            scheme = null,
            host = null,
            path = null,
            queryParams = emptyMap(),
            fragment = null,
            rawUri = uri
        )
        assertEquals(expected, result)
    }
}
