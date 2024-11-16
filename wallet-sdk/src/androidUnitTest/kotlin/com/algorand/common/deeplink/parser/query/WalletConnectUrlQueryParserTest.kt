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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WalletConnectUrlQueryParserTest {

    private val sut = WalletConnectUrlQueryParser()

    @Test
    fun `EXPECT null WHEN raw uri is empty`() {
        val uri = PeraUriBuilder.create(rawUri = "")

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT WC connection uri WHEN raw uri is applink with wc connection uri`() {
        val uri = PeraUriBuilder.create(
            host = "perawallet.app",
            rawUri = "https://perawallet.app/qr/perawallet-wc/wc:connection_uri"
        )

        val result = sut.parseQuery(uri)

        val expected = "wc:connection_uri"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT WC connection uri WHEN raw uri is pera deep link with connection uri`() {
        val uri = PeraUriBuilder.create(rawUri = "perawallet://wc:connection_uri")

        val result = sut.parseQuery(uri)

        val expected = "wc:connection_uri"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT WC connection uri WHEN raw uri is wc deep link`() {
        val uri = PeraUriBuilder.create(rawUri = "wc://wc:connection_uri")

        val result = sut.parseQuery(uri)

        val expected = "wc:connection_uri"
        assertEquals(expected, result)
    }
}