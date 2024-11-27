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

package com.algorand.common.deeplink.parser.query

import com.algorand.common.deeplink.PeraUriBuilder
import com.algorand.common.encryption.Base64Manager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UrlQueryParserTest {

    private val base64Manager: Base64Manager = mockk()
    private val sut = UrlQueryParser(base64Manager)

    @Test
    fun `EXPECT null WHEN url query does not exist`() {
        val uri = PeraUriBuilder.create(queryParams = emptyMap())

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN url query can not be decoded`() {
        every { base64Manager.decode("invalid") } throws Exception()
        val uri = PeraUriBuilder.create(queryParams = mapOf("url" to "invalid"))

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN decoded url is empty`() {
        every { base64Manager.decode("invalid") } returns ByteArray(0)
        val uri = PeraUriBuilder.create(queryParams = mapOf("url" to "invalid"))

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT url WHEN url query can be decoded`() {
        every { base64Manager.decode("valid") } returns "valid_url".toByteArray()
        val uri = PeraUriBuilder.create(queryParams = mapOf("url" to "valid"))

        val result = sut.parseQuery(uri)

        assertEquals("valid_url", result)
    }
}
