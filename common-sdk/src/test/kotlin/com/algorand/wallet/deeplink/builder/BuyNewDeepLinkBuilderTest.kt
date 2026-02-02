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
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class BuyNewDeepLinkBuilderTest {

    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk()
    private val sut: BuyNewDeepLinkBuilder = BuyNewDeepLinkBuilder(isFeatureToggleEnabled)

    @Test
    fun `EXPECT buy deeplink with empty address and path WHEN feature toggle is enabled`() {
        every { isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key) } returns true
        val deepLinkWithPath = VALID_DEEP_LINK.copy(path = "customPath")

        val result = sut.createDeepLink(deepLinkWithPath)

        val expected = DeepLink.Buy(address = "", path = "customPath")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT buy deeplink with empty address and null path WHEN feature toggle is enabled and no path`() {
        every { isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key) } returns true

        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.Buy(address = "", path = null)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT buy deeplink with address and null path WHEN feature toggle is disabled and account address exists`() {
        every { isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key) } returns false

        val result = sut.createDeepLink(VALID_DEEP_LINK)

        val expected = DeepLink.Buy(address = "accountAddress", path = null)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT buy deeplink with address and null path WHEN feature toggle is disabled and path is ignored`() {
        every { isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key) } returns false
        val deepLinkWithPath = VALID_DEEP_LINK.copy(path = "customPath")

        val result = sut.createDeepLink(deepLinkWithPath)

        val expected = DeepLink.Buy(address = "accountAddress", path = null)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN feature toggle is disabled and account address is null`() {
        every { isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key) } returns false
        val invalidDeepLink = VALID_DEEP_LINK.copy(accountAddress = null)

        val result = sut.createDeepLink(invalidDeepLink)

        assertNull(result)
    }

    private companion object {
        val VALID_DEEP_LINK = DeepLinkPayload(
            accountAddress = "accountAddress",
            rawDeepLinkUri = ""
        )
    }
}