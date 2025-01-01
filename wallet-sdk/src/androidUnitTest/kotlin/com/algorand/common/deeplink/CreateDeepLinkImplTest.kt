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

package com.algorand.common.deeplink

import com.algorand.common.deeplink.builder.DeepLinkBuilder
import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.DeepLinkPayload
import com.algorand.common.deeplink.model.NotificationGroupType
import com.algorand.common.deeplink.parser.CreateDeepLinkImpl
import com.algorand.common.deeplink.parser.ParseDeepLinkPayload
import com.algorand.common.testing.peraFixture
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateDeepLinkImplTest {

    private val parseDeepLinkPayload: ParseDeepLinkPayload = mockk()

    private val accountAddressDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val assetOptInDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val assetTransferDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val mnemonicDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val walletConnectConnectionDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val webImportQrCodeDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val notificationGroupDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val discoverBrowserDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val discoverDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val assetInboxDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val keyRegTransactionDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val cardsDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }
    private val stakingDeepLinkBuilder: DeepLinkBuilder = mockk {
        every { doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns false
    }

    private val sut = CreateDeepLinkImpl(
        parseDeepLinkPayload,
        accountAddressDeepLinkBuilder,
        assetOptInDeepLinkBuilder,
        assetTransferDeepLinkBuilder,
        mnemonicDeepLinkBuilder,
        walletConnectConnectionDeepLinkBuilder,
        webImportQrCodeDeepLinkBuilder,
        notificationGroupDeepLinkBuilder,
        discoverBrowserDeepLinkBuilder,
        discoverDeepLinkBuilder,
        assetInboxDeepLinkBuilder,
        keyRegTransactionDeepLinkBuilder,
        cardsDeepLinkBuilder,
        stakingDeepLinkBuilder
    )

    @Test
    fun `EXPECT account address deep link`() {
        val deepLink = DeepLink.AccountAddress("address", "label")
        every { accountAddressDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { accountAddressDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("accountAddressDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("accountAddressDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT asset opt in deep link`() {
        val deepLink = DeepLink.AssetOptIn(1234)
        every { assetOptInDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { assetOptInDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("assetOptInDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("assetOptInDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT asset transfer deep link`() {
        val deepLink = DeepLink.AssetTransfer(
            assetId = 1234,
            amount = "100",
            note = "note",
            xnote = "xnote",
            receiverAccountAddress = "receiverAccountAddress",
            label = null
        )
        every { assetTransferDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { assetTransferDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("assetTransferDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("assetTransferDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT discover browser deep link`() {
        val deepLink = DeepLink.DiscoverBrowser("webUrl")
        every { discoverBrowserDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { discoverBrowserDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("discoverBrowserDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("discoverBrowserDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT discover deep link`() {
        val deepLink = DeepLink.Discover("path")
        every { discoverDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { discoverDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("discoverDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("discoverDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT mnemonic deep link`() {
        val deepLink = DeepLink.Mnemonic("mnemonic")
        every { mnemonicDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { mnemonicDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("mnemonicDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("mnemonicDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT notification deep link`() {
        val deepLink = DeepLink.Notification("address", 1234, NotificationGroupType.TRANSACTIONS)
        every { notificationGroupDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { notificationGroupDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("notificationGroupDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("notificationGroupDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT wallet connect connection deep link`() {
        val deepLink = DeepLink.WalletConnectConnection("walletConnectUrl")
        every { walletConnectConnectionDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { walletConnectConnectionDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("walletConnectConnectionDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("walletConnectConnectionDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT web import qr code deep link`() {
        val deepLink = DeepLink.WebImportQrCode("backupId", "encryptionKey")
        every { webImportQrCodeDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { webImportQrCodeDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink
        every { parseDeepLinkPayload("webImportQrCodeDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("webImportQrCodeDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT asset inbox deep link`() {
        val deepLink = DeepLink.AssetInbox("address", NotificationGroupType.ASSET_INBOX)
        every { parseDeepLinkPayload("assetInboxDeepLink") } returns DEEP_LINK_PAYLOAD
        every { assetInboxDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { assetInboxDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink

        val result = sut("assetInboxDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT key reg transaction deep link`() {
        val deepLink = peraFixture<DeepLink.KeyReg>()
        every { parseDeepLinkPayload("keyRegTransactionDeepLink") } returns DEEP_LINK_PAYLOAD
        every { keyRegTransactionDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { keyRegTransactionDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink

        val result = sut("keyRegTransactionDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT cards deep link`() {
        val deepLink = DeepLink.Cards("path")
        every { parseDeepLinkPayload("cardsDeepLink") } returns DEEP_LINK_PAYLOAD
        every { cardsDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { cardsDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink

        val result = sut("cardsDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT staking deep link`() {
        val deepLink = DeepLink.Staking("path")
        every { parseDeepLinkPayload("stakingDeepLink") } returns DEEP_LINK_PAYLOAD
        every { stakingDeepLinkBuilder.doesDeeplinkMeetTheRequirements(DEEP_LINK_PAYLOAD) } returns true
        every { stakingDeepLinkBuilder.createDeepLink(DEEP_LINK_PAYLOAD) } returns deepLink

        val result = sut("stakingDeepLink")

        assertEquals(deepLink, result)
    }

    @Test
    fun `EXPECT undefined deep link WHEN deep link is not recognized`() {
        every { parseDeepLinkPayload("undefinedDeepLink") } returns DEEP_LINK_PAYLOAD

        val result = sut("undefinedDeepLink")

        val expected = DeepLink.Undefined("undefinedDeepLink")
        assertEquals(expected, result)
    }

    private companion object {
        val DEEP_LINK_PAYLOAD = DeepLinkPayload(
            walletConnectUrl = null,
            accountAddress = null,
            assetId = null,
            amount = null,
            note = null,
            url = null,
            xnote = null,
            label = null,
            webImportQrCode = null,
            notificationGroupType = null,
            rawDeepLinkUri = "deep link"
        )
    }
}
