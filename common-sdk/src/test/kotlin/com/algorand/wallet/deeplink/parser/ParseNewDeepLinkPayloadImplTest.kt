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

package com.algorand.wallet.deeplink.parser

import com.algorand.wallet.deeplink.PeraUriBuilder
import com.algorand.wallet.deeplink.model.DeepLinkPayload
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class ParseNewDeepLinkPayloadImplTest {

    private val peraUriParser: PeraNewUriParser = mockk()

    private val sut = ParseNewDeepLinkPayloadImpl(
        peraUriParser
    )

    @Test
    fun `EXPECT deep link payload`() {
        every { peraUriParser.parseUri(URI) } returns PERA_URI

        val result = sut(URI)

        val expected = DeepLinkPayload(
            accountAddress = "address",
            walletConnectUrl = "walletConnectUrl",
            assetId = 1L,
            amount = "1",
            note = "note",
            xnote = "xnote",
            label = "label",
            transactionId = "transactionId",
            transactionStatus = "transactionStatus",
            mnemonic = "mnemonic",
            url = "url",
            webImportQrCode = null,
            notificationGroupType = null,
            fee = "1",
            votekey = "votekey",
            selkey = "selkey",
            sprfkey = "sprfkey",
            votefst = "votefst",
            votelst = "votelst",
            votekd = "votekd",
            type = "type",
            host = "perawallet.app",
            path = "path",
            rawDeepLinkUri = URI,
            assetInId = 1000,
            assetOutId = 2000,
            backupId = "backupId",
            encryptionKey = "encryptionKey",
            action = "action",
            receiverAddress = "receiverAddress",
            lastPathSegment = null
        )
        assertEquals(expected, result)
    }

    private companion object {
        // Keep param order stable to avoid string-diff noise
        const val URI =
            "pera://perawallet.app/app/?" +
                    "address=address" +
                    "&walletConnectUrl=walletConnectUrl" +
                    "&assetId=1" +
                    "&amount=1" +
                    "&note=note" +
                    "&xnote=xnote" +
                    "&label=label" +
                    "&transactionId=transactionId" +
                    "&transactionStatus=transactionStatus" +
                    "&mnemonic=mnemonic" +
                    "&url=url" +
                    "&webImportQrCode=webImportQrCode" +
                    "&notificationGroupType=TRANSACTIONS" +
                    "&type=type" +
                    "&selkey=selkey" +
                    "&sprfkey=sprfkey" +
                    "&votefst=votefst" +
                    "&votelst=votelst" +
                    "&votekd=votekd" +
                    "&votekey=votekey" +
                    "&path=path" +
                    "&fee=1" +
                    "&action=action" +
                    "&receiverAddress=receiverAddress" +
                    "&assetInId=1000" +
                    "&assetOutId=2000" +
                    "&backupId=backupId" +
                    "&encryptionKey=encryptionKey"

        val PERA_URI = PeraUriBuilder.create(
            scheme = "https",
            host = "perawallet.app",
            path = "app",
            queryParams = mapOf(
                "address" to "address",
                "walletConnectUrl" to "walletConnectUrl",
                "assetId" to "1",
                "amount" to "1",
                "note" to "note",
                "xnote" to "xnote",
                "label" to "label",
                "transactionId" to "transactionId",
                "transactionStatus" to "transactionStatus",
                "mnemonic" to "mnemonic",
                "url" to "url",
                "webImportQrCode" to "webImportQrCode",
                "notificationGroupType" to "TRANSACTIONS",
                "type" to "type",
                "selkey" to "selkey",
                "sprfkey" to "sprfkey",
                "votefst" to "votefst",
                "votelst" to "votelst",
                "votekd" to "votekd",
                "votekey" to "votekey",
                "path" to "path",
                "fee" to "1",
                "action" to "action",
                "receiverAddress" to "receiverAddress",
                "assetInId" to "1000",
                "assetOutId" to "2000",
                "backupId" to "backupId",
                "encryptionKey" to "encryptionKey"
            ),
            rawUri = URI
        )
    }
}