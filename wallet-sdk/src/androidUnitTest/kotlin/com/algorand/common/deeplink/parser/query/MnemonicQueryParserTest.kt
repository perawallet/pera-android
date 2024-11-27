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

class MnemonicQueryParserTest {

    private val sut = MnemonicQueryParser()

    @Test
    fun `EXPECT null WHEN uri is not for mnemonic`() {
        val uri = PeraUriBuilder.create(rawUri = "INVALID_URI")

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT mnemonic WHEN uri is mnemonic payload json`() {
        val uri = PeraUriBuilder.create(rawUri = VALID_URI)

        val result = sut.parseQuery(uri)

        val expected = "valid_mnemonic"
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_URI = """
            {
                "version": 1,
                "mnemonic": "valid_mnemonic"
            }
        """.trimIndent()
    }
}
