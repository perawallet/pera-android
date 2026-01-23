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

package com.algorand.wallet.inbox.domain.usecase

import com.algorand.wallet.inbox.domain.model.AssetInbox
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class HasInboxItemsForAddressUseCaseTest {

    private val getInboxMessages: GetInboxMessages = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk {
        every { this@mockk(any()) } returns false
    }

    @Test
    fun `EXPECT false WHEN inbox messages is null`() = runTest {
        coEvery { getInboxMessages() } returns null
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN has asset inbox with matching address and count greater than 0`() = runTest {
        coEvery { getInboxMessages() } returns createInboxMessages(
            assetInboxes = listOf(AssetInbox(address = TEST_ADDRESS, inboxAddress = null, requestCount = 5))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN has asset inbox with matching address but count is 0`() = runTest {
        coEvery { getInboxMessages() } returns createInboxMessages(
            assetInboxes = listOf(AssetInbox(address = TEST_ADDRESS, inboxAddress = null, requestCount = 0))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN has asset inbox with different address`() = runTest {
        coEvery { getInboxMessages() } returns createInboxMessages(
            assetInboxes = listOf(AssetInbox(address = "OTHER", inboxAddress = null, requestCount = 5))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN joint account disabled and has invitation`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns false
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountImportRequests = listOf(createJointAccount(participantAddresses = listOf(TEST_ADDRESS)))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN joint account enabled and has invitation with address as participant`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountImportRequests = listOf(createJointAccount(participantAddresses = listOf(TEST_ADDRESS, "OTHER")))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN joint account enabled and has invitation but address is not participant`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountImportRequests = listOf(createJointAccount(participantAddresses = listOf("OTHER1", "OTHER2")))
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN joint account enabled and has sign request where address is joint account`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountSignRequests = listOf(
                createSignRequest(jointAccountAddress = TEST_ADDRESS, participantAddresses = listOf("P1", "P2"))
            )
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN joint account enabled and has sign request where address is participant`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountSignRequests = listOf(
                createSignRequest(jointAccountAddress = "JOINT", participantAddresses = listOf(TEST_ADDRESS, "OTHER"))
            )
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN joint account enabled and has sign request but address not related`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            jointAccountSignRequests = listOf(
                createSignRequest(jointAccountAddress = "OTHER_JOINT", participantAddresses = listOf("P1", "P2"))
            )
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN all lists are empty`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        coEvery { getInboxMessages() } returns createInboxMessages(
            assetInboxes = emptyList(),
            jointAccountImportRequests = emptyList(),
            jointAccountSignRequests = emptyList()
        )
        val sut = createUseCase()

        val result = sut(TEST_ADDRESS)

        assertFalse(result)
    }

    private fun createUseCase() = HasInboxItemsForAddressUseCase(getInboxMessages, isFeatureToggleEnabled)

    private fun createInboxMessages(
        assetInboxes: List<AssetInbox>? = null,
        jointAccountImportRequests: List<JointAccount>? = null,
        jointAccountSignRequests: List<JointSignRequest>? = null
    ) = InboxMessages(
        assetInboxes = assetInboxes,
        jointAccountImportRequests = jointAccountImportRequests,
        jointAccountSignRequests = jointAccountSignRequests
    )

    private fun createJointAccount(participantAddresses: List<String>) = JointAccount(
        creationDatetime = null,
        address = "JOINT_ADDRESS",
        version = 1,
        threshold = 2,
        participantAddresses = participantAddresses
    )

    private fun createSignRequest(
        jointAccountAddress: String,
        participantAddresses: List<String>
    ) = JointSignRequest(
        id = "1",
        jointAccount = JointAccount(
            creationDatetime = null,
            address = jointAccountAddress,
            version = 1,
            threshold = 2,
            participantAddresses = participantAddresses
        ),
        proposerAddress = "PROPOSER",
        type = "transfer",
        rawTransactionLists = null,
        transactionLists = null,
        expectedExpireDatetime = null,
        status = null
    )

    private companion object {
        const val TEST_ADDRESS = "TEST_ADDRESS"
    }
}
