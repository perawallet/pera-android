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
import org.junit.Assert.assertNull
import org.junit.Test

class DiscoverPathNewDeepLinkBuilderTest {

    private val sut = DiscoverPathNewDeepLinkBuilder()

    @Test
    fun `EXPECT discover deeplink WHEN path provided`() {
        val payload = DeepLinkPayload(path = "/test/discover/path", rawDeepLinkUri = "")

        val result = sut.createDeepLink(payload)

        val expected = DeepLink.Discover("/test/discover/path")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN path not provided`() {
        val payload = DeepLinkPayload(rawDeepLinkUri = "")

        val result = sut.createDeepLink(payload)

        assertNull(result)
    }
}