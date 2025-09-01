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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletConnectNewDeepLinkBuilderTest {

    private val sut = WalletConnectNewDeepLinkBuilder()

    @Test
    fun `EXPECT true WHEN deeplink requirements match`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK) != null

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN deeplink requirements do not match`() {
        val invalidDeepLink = VALID_DEEP_LINK.copy(walletConnectUrl = null)

        val result = sut.createDeepLink(invalidDeepLink) != null

        assertFalse(result)
    }

    @Test
    fun `EXPECT wallet connect deeplink`() {
        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.WalletConnectConnection(
            uri = "wc:c3644ba1-b3fd-4795-8e2b-50343a7a829f@1?bridge=https%3A%2F%2Fwallet-connect-g.perawallet.app&key=b3ba08f77de57951c9fe198814b08cbb215480ba0913cc01b54af391bf265949&algorand=true"
        )
        assertEquals(expected, result)
    }

    private companion object {
        val VALID_DEEP_LINK = DeepLinkPayload(
            walletConnectUrl = "d2M6YzM2NDRiYTEtYjNmZC00Nzk1LThlMmItNTAzNDNhN2E4MjlmQDE/YnJpZGdlPWh0dHBzJTNBJTJGJTJGd2FsbGV0LWNvbm5lY3QtZy5wZXJhd2FsbGV0LmFwcCZrZXk9YjNiYTA4Zjc3ZGU1Nzk1MWM5ZmUxOTg4MTRiMDhjYmIyMTU0ODBiYTA5MTNjYzAxYjU0YWYzOTFiZjI2NTk0OSZhbGdvcmFuZD10cnVl",
            rawDeepLinkUri = ""
        )
    }
}