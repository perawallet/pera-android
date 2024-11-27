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

package com.algorand.common.deeplink.builder

import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.DeepLinkPayload
import com.algorand.common.deeplink.model.NotificationGroupType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationDeepLinkBuilderTest {

    private val sut = NotificationDeepLinkBuilder()

    @Test
    fun `EXPECT false WHEN deep link requirements do not meet`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(url = "12321")

        val result = sut.doesDeeplinkMeetTheRequirements(invalidDeepLink)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN deep link requirements meet`() {
        val result = sut.doesDeeplinkMeetTheRequirements(VALID_DEEP_LINK)

        assertTrue(result)
    }

    @Test
    fun `EXPECT notification deep link`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.Notification(
            address = "address",
            assetId = 1234,
            notificationGroupType = NotificationGroupType.TRANSACTIONS
        )
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_DEEP_LINK = DeepLinkPayload(
            accountAddress = "address",
            assetId = 1234,
            notificationGroupType = NotificationGroupType.TRANSACTIONS,
            amount = null,
            walletConnectUrl = null,
            url = null,
            note = null,
            xnote = null,
            label = null,
            webImportQrCode = null,
            rawDeepLinkUri = ""
        )
    }
}
