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

package com.algorand.wallet.deeplink.builder

import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.DeepLinkPayload
import com.algorand.wallet.deeplink.model.NotificationGroupType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetInboxDeepLinkBuilderTest {

    private val sut = AssetInboxDeepLinkBuilder()

    @Test
    fun `EXPECT true WHEN deeplink requirements match`() {
        val result = sut.doesDeeplinkMeetTheRequirements(VALID_DEEP_LINK)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN notificationGroupType is null`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(notificationGroupType = null)

        val result = sut.doesDeeplinkMeetTheRequirements(invalidDeepLink)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN accountAddress is null`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(accountAddress = null)

        val result = sut.doesDeeplinkMeetTheRequirements(invalidDeepLink)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN notificationGroupType is not ASSET_INBOX`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(notificationGroupType = NotificationGroupType.TRANSACTIONS)

        val result = sut.doesDeeplinkMeetTheRequirements(invalidDeepLink)

        assertFalse(result)
    }

    @Test
    fun `EXPECT asset inbox deeplink WHEN requirements match`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.AssetInbox(
            address = "accountAddress"
        )
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_DEEP_LINK = DeepLinkPayload(
            accountAddress = "accountAddress",
            notificationGroupType = NotificationGroupType.ASSET_INBOX,
            rawDeepLinkUri = ""
        )
    }
}