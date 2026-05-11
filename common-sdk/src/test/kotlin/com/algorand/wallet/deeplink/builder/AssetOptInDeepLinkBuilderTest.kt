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
import com.algorand.wallet.deeplink.model.WebImportQrCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetOptInDeepLinkBuilderTest {

    private val sut = AssetOptInDeepLinkBuilder()

    @Test
    fun `EXPECT true WHEN deeplink requirements match`() {
        val result = sut.doesDeeplinkMeetTheRequirements(VALID_DEEP_LINK_PAYLOAD)
        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN assetId is null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(assetId = null)
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN amount is not zero`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(amount = "1")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN amount is null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(amount = null)
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN accountAddress is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(accountAddress = "someAddress")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN walletConnectUrl is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(walletConnectUrl = "someWCUrl")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN note is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(note = "someNote")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN xnote is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(xnote = "someXnote")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN url is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(url = "someUrl")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN label is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(label = "someLabel")
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN webImportQrCode is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(webImportQrCode = WebImportQrCode("", ""))
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN notificationGroupType is not null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(notificationGroupType = NotificationGroupType.TRANSACTIONS)
        val result = sut.doesDeeplinkMeetTheRequirements(payload)
        assertFalse(result)
    }

    @Test
    fun `EXPECT asset opt in deeplink WHEN assetId is not null`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK_PAYLOAD)
        val expected =
            DeepLink.AssetOptIn(VALID_DEEP_LINK_PAYLOAD.assetId ?: return, VALID_DEEP_LINK_PAYLOAD.accountAddress)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT undefined deeplink WHEN assetId is null`() {
        val payload = VALID_DEEP_LINK_PAYLOAD.copy(assetId = null)
        val result = sut.createDeepLink(payload)
        val expected = DeepLink.Undefined(payload.rawDeepLinkUri)
        assertEquals(expected, result)
    }

    private companion object {
        private const val DEFAULT_ASSET_ID = 12345L
        private const val DEFAULT_RAW_DEEP_LINK_URI = "pera://pera.com/swap?asset_in=123&asset_out=0"
        val VALID_DEEP_LINK_PAYLOAD = DeepLinkPayload(
            assetId = DEFAULT_ASSET_ID,
            amount = "0",
            rawDeepLinkUri = DEFAULT_RAW_DEEP_LINK_URI
        )
    }
}