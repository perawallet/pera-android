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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class AccountAddressDeepLinkBuilderTest {

    private val sut = AccountAddressDeepLinkBuilder()

    @Test
    fun `EXPECT true WHEN deeplink requirements match`() {
        val result = sut.doesDeeplinkMeetTheRequirements(VALID_DEEP_LINK)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN deeplink requirements do not match`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(walletConnectUrl = "walletConnectUrl")

        val result = sut.doesDeeplinkMeetTheRequirements(invalidDeepLink)

        assertFalse(result)
    }

    @Test
    fun `EXPECT account address deeplink`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.AccountAddress(
            address = "accountAddress",
            label = "label"
        )
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_DEEP_LINK = DeepLinkPayload(
            accountAddress = "accountAddress",
            walletConnectUrl = null,
            assetId = null,
            amount = null,
            note = null,
            xnote = null,
            url = null,
            label = "label",
            webImportQrCode = null,
            notificationGroupType = null,
            rawDeepLinkUri = ""
        )
    }
}
