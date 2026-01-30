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

internal class JointAccountImportDeepLinkBuilderTest {

    private val sut = JointAccountImportDeepLinkBuilder()

    @Test
    fun `EXPECT true WHEN host and address match requirements`() {
        val payload = createPayload(host = VALID_HOST, address = TEST_ADDRESS)

        val result = sut.doesDeeplinkMeetTheRequirements(payload)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN host does not match`() {
        val payload = createPayload(host = "something-else", address = TEST_ADDRESS)

        val result = sut.doesDeeplinkMeetTheRequirements(payload)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN address is null`() {
        val payload = createPayload(host = VALID_HOST, address = null)

        val result = sut.doesDeeplinkMeetTheRequirements(payload)

        assertFalse(result)
    }

    @Test
    fun `EXPECT joint account import deep link WHEN createDeepLink is called`() {
        val payload = createPayload(host = VALID_HOST, address = TEST_ADDRESS)

        val result = sut.createDeepLink(payload)

        val expected = DeepLink.JointAccountImport(address = TEST_ADDRESS)
        assertEquals(expected, result)
    }

    private fun createPayload(host: String?, address: String?) = DeepLinkPayload(
        host = host,
        accountAddress = address,
        rawDeepLinkUri = ""
    )

    private companion object {
        const val VALID_HOST = "joint-account-import"
        const val TEST_ADDRESS = "JOINT_ACCOUNT_ADDRESS"
    }
}
