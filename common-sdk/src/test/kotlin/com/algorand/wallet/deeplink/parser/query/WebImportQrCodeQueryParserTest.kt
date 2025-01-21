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

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.deeplink.PeraUriBuilder
import com.algorand.wallet.deeplink.model.WebImportQrCode
import com.algorand.wallet.foundation.json.JsonSerializer
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WebImportQrCodeQueryParserTest {

    private val jsonSerializer: JsonSerializer = mockk(relaxed = true)
    private val sut = WebImportQrCodeQueryParser(jsonSerializer)

    @Test
    fun `EXPECT null WHEN parsing payload throws exception`() {
        every { jsonSerializer.fromJson("INVALID_URI", WebImportQrCode::class.java) } throws Exception()

        val uri = PeraUriBuilder.create(rawUri = "INVALID_URI")

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN payload is valid but version is missing`() {
        val uri = PeraUriBuilder.create(
            rawUri = """
            {
                "action": "import",
                "platform": "android",
                "backupId": "b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57",
                "modificationKey": null,
                "encryptionKey": "KJDH374DSFJH8F9SDKJF"
            }
        """.trimIndent()
        )

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN payload is valid but version is bigger than expected version`() {
        val uri = PeraUriBuilder.create(
            rawUri = """
            {
                "version": "2",
                "action": "import",
                "platform": "android",
                "backupId": "b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57",
                "modificationKey": null,
                "encryptionKey": "KJDH374DSFJH8F9SDKJF"
            }
        """.trimIndent()
        )

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN payload and version is valid but action is not import`() {
        val uri = PeraUriBuilder.create(
            rawUri = """
            {
                "version": "1",
                "action": "export",
                "platform": "android",
                "backupId": "b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57",
                "modificationKey": null,
                "encryptionKey": "KJDH374DSFJH8F9SDKJF"
            }
        """.trimIndent()
        )

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT web import qr code WHEN payload version and action is valid`() {
        every {
            jsonSerializer.fromJson(VALID_URI, WebImportQrCodeQueryParser.WebQrCode::class.java)
        } returns WEB_QR_CODE
        val uri = PeraUriBuilder.create(rawUri = VALID_URI)

        val result = sut.parseQuery(uri)

        val expected = WebImportQrCode("b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57", "KJDH374DSFJH8F9SDKJF")
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_URI = """
            {
                "version": "1",
                "action": "import",
                "platform": "android",
                "backupId": "b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57",
                "modificationKey": null,
                "encryptionKey": "KJDH374DSFJH8F9SDKJF"
            }
        """.trimIndent()

        val WEB_QR_CODE = WebImportQrCodeQueryParser.WebQrCode(
            version = "1",
            action = "import",
            platform = "android",
            backupId = "b7c3f5a9-8e44-4d27-92c3-a7f4f3341e57",
            modificationKey = null,
            encryptionKey = "KJDH374DSFJH8F9SDKJF"
        )
    }
}
