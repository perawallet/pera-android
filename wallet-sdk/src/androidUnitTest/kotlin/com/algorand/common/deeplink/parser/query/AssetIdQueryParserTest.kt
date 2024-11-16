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
import kotlin.test.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetIdQueryParserTest {

    private val sut = AssetIdQueryParser()

    @Test
    fun `EXPECT asset id WHEN query contains asset id`() {
        val uri = PeraUriBuilder.create(
            queryParams = mapOf("asset" to "123")
        )
        val result = sut.parseQuery(uri)

        assertEquals(123L, result)
    }

    @Test
    fun `EXPECT asset id WHEN uri is coinbase uri and has asset id`() {
        val uri = PeraUriBuilder.create(
            scheme = "algo",
            host = "31566704",
            path = "transfer",
            queryParams = mapOf("address" to "KG2HXWIOQSBOBGJEXSIBNEVNTRD4G4EFIJGRKBG2ZOT7NQ"),
            rawUri = "algo:31566704/transfer?address=KG2HXWIOQSBOBGJEXSIBNEVNTRD4G4EFIJGRKBG2ZOT7NQ"
        )

        val result = sut.parseQuery(uri)

        assertEquals(31566704L, result)
    }

    @Test
    fun `EXPECT null WHEN uri contains invalid asset id`() {
        val uri = PeraUriBuilder.create(
            queryParams = mapOf("asset" to "invalid")
        )
        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN uri does not contain asset id`() {
        val uri = PeraUriBuilder.create()

        val result = sut.parseQuery(uri)

        assertNull(result)
    }
}
