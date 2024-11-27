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

import com.algorand.common.deeplink.PeraUriBuilder
import com.algorand.common.deeplink.model.DeepLinkPayload
import com.algorand.common.deeplink.model.NotificationGroupType
import com.algorand.common.deeplink.model.WebImportQrCode
import com.algorand.common.deeplink.parser.query.DeepLinkQueryParser
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import org.junit.Test

class ParseDeepLinkPayloadImplTest {

    private val peraUriParser: PeraUriParser = mockk()
    private val accountAddressQueryParser: DeepLinkQueryParser<String?> = mockk()
    private val assetIdQueryParser: DeepLinkQueryParser<Long?> = mockk()
    private val notificationGroupTypeQueryParser: DeepLinkQueryParser<NotificationGroupType?> = mockk()
    private val webImportQrCodeQueryParser: DeepLinkQueryParser<WebImportQrCode?> = mockk()
    private val urlQueryParser: DeepLinkQueryParser<String?> = mockk()
    private val mnemonicQueryParser: DeepLinkQueryParser<String?> = mockk()
    private val walletConnectUrlQueryParser: DeepLinkQueryParser<String?> = mockk()

    private val sut = ParseDeepLinkPayloadImpl(
        peraUriParser,
        accountAddressQueryParser,
        assetIdQueryParser,
        notificationGroupTypeQueryParser,
        webImportQrCodeQueryParser,
        urlQueryParser,
        mnemonicQueryParser,
        walletConnectUrlQueryParser
    )

    @Test
    fun `EXPECT deep link payload`() {
        every { peraUriParser.parseUri(URI) } returns PERA_URI
        every { accountAddressQueryParser.parseQuery(PERA_URI) } returns "accountAddress"
        every { assetIdQueryParser.parseQuery(PERA_URI) } returns 1
        every { notificationGroupTypeQueryParser.parseQuery(PERA_URI) } returns NotificationGroupType.TRANSACTIONS
        every {
            webImportQrCodeQueryParser.parseQuery(PERA_URI)
        } returns WebImportQrCode("webImportQrCode", "webImportQrCode")
        every { urlQueryParser.parseQuery(PERA_URI) } returns "url"
        every { mnemonicQueryParser.parseQuery(PERA_URI) } returns "mnemonic"
        every { walletConnectUrlQueryParser.parseQuery(PERA_URI) } returns "walletConnectUrl"

        val result = sut(URI)

        val expected = DeepLinkPayload(
            accountAddress = "accountAddress",
            walletConnectUrl = "walletConnectUrl",
            assetId = 1,
            amount = "1",
            note = "note",
            xnote = "xnote",
            label = "label",
            transactionId = "transactionId",
            transactionStatus = "transactionStatus",
            mnemonic = "mnemonic",
            url = "url",
            webImportQrCode = WebImportQrCode("webImportQrCode", "webImportQrCode"),
            notificationGroupType = NotificationGroupType.TRANSACTIONS,
            rawDeepLinkUri = URI
        )
        assertEquals(expected, result)
    }

    private companion object {
        const val URI = "some uri"
        val PERA_URI = PeraUriBuilder.create(
            scheme = "https",
            host = "perawallet.app",
            path = "",
            queryParams = mapOf(
                "amount" to "1",
                "note" to "note",
                "xnote" to "xnote",
                "label" to "label",
                "transactionId" to "transactionId",
                "transactionStatus" to "transactionStatus"
            ),
            fragment = "",
            rawUri = ""
        )
    }
}
