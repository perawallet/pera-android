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

package com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel

import com.algorand.android.models.User
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountdetail.jointaccountdetail.domain.usecase.CreateJointAccountParticipantItem
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.repository.ContactRepository
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount as JointAccountDto
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DefaultJointAccountDetailProcessorTest {

    private val getJointAccount: GetJointAccount = mockk()
    private val getJointAccountDetail: GetJointAccountDetail = mockk()
    private val getAccountDisplayName: GetAccountDisplayName = mockk()
    private val contactRepository: ContactRepository = mockk()
    private val createJointAccountParticipantItem: CreateJointAccountParticipantItem = mockk()
    private val getInboxMessages: GetInboxMessages = mockk()
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId = mockk()
    private val inboxApiRepository: InboxApiRepository = mockk()

    private val sut = DefaultJointAccountDetailProcessor(
        getJointAccount = getJointAccount,
        getJointAccountDetail = getJointAccountDetail,
        getAccountDisplayName = getAccountDisplayName,
        contactRepository = contactRepository,
        createJointAccountParticipantItem = createJointAccountParticipantItem,
        getInboxMessages = getInboxMessages,
        getSelectedNodeDeviceId = getSelectedNodeDeviceId,
        inboxApiRepository = inboxApiRepository
    )

    // region createContentState Tests

    @Test
    fun `EXPECT content state with correct values WHEN createContentState called`() = runTest {
        val jointAccount = createJointAccount()
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Test Account"
        }
        val participantItem = createParticipantItem("ADDR1")

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { createJointAccountParticipantItem(any()) } returns participantItem

        val result = sut.createContentState(jointAccount, TEST_ADDRESS, showActions = false)

        assertEquals("Test Account", result.accountDisplayName)
        assertEquals(DEFAULT_THRESHOLD, result.threshold)
        assertEquals(DEFAULT_PARTICIPANTS.size, result.numberOfAccounts)
        assertFalse(result.showActions)
    }

    @Test
    fun `EXPECT showActions true WHEN createContentState called with showActions true`() = runTest {
        val jointAccount = createJointAccount()
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Test Account"
        }
        val participantItem = createParticipantItem("ADDR1")

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { createJointAccountParticipantItem(any()) } returns participantItem

        val result = sut.createContentState(jointAccount, TEST_ADDRESS, showActions = true)

        assertTrue(result.showActions)
    }

    // endregion

    // region createContentStateFromInvitation Tests

    @Test
    fun `EXPECT content state from invitation WHEN createContentStateFromInvitation called`() = runTest {
        val participantItem = createParticipantItem("ADDR1")

        coEvery { createJointAccountParticipantItem(any()) } returns participantItem

        val result = sut.createContentStateFromInvitation(
            participantAddresses = DEFAULT_PARTICIPANTS,
            threshold = DEFAULT_THRESHOLD,
            accountAddress = TEST_ADDRESS
        )

        assertEquals(DEFAULT_THRESHOLD, result.threshold)
        assertEquals(DEFAULT_PARTICIPANTS.size, result.numberOfAccounts)
        assertTrue(result.showActions)
    }

    // endregion

    // region fetchInvitationFromInbox Tests

    @Test
    fun `EXPECT NotFound WHEN getInboxMessages returns null`() = runTest {
        coEvery { getInboxMessages() } returns null

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.NotFound)
    }

    @Test
    fun `EXPECT NotFound WHEN jointAccountImportRequests is null`() = runTest {
        val inboxMessages = mockk<InboxMessages> {
            every { jointAccountImportRequests } returns null
        }
        coEvery { getInboxMessages() } returns inboxMessages

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.NotFound)
    }

    @Test
    fun `EXPECT NotFound WHEN no matching address found`() = runTest {
        val importRequest = JointAccountDto(
            creationDatetime = null,
            address = "OTHER_ADDRESS",
            version = 1,
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS
        )
        val inboxMessages = mockk<InboxMessages> {
            every { jointAccountImportRequests } returns listOf(importRequest)
        }
        coEvery { getInboxMessages() } returns inboxMessages

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.NotFound)
    }

    @Test
    fun `EXPECT NotFound WHEN participantAddresses is empty`() = runTest {
        val importRequest = JointAccountDto(
            creationDatetime = null,
            address = TEST_ADDRESS,
            version = 1,
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = emptyList()
        )
        val inboxMessages = mockk<InboxMessages> {
            every { jointAccountImportRequests } returns listOf(importRequest)
        }
        coEvery { getInboxMessages() } returns inboxMessages

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.NotFound)
    }

    @Test
    fun `EXPECT NotFound WHEN threshold is null`() = runTest {
        val importRequest = JointAccountDto(
            creationDatetime = null,
            address = TEST_ADDRESS,
            version = 1,
            threshold = null,
            participantAddresses = DEFAULT_PARTICIPANTS
        )
        val inboxMessages = mockk<InboxMessages> {
            every { jointAccountImportRequests } returns listOf(importRequest)
        }
        coEvery { getInboxMessages() } returns inboxMessages

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.NotFound)
    }

    @Test
    fun `EXPECT Success WHEN invitation found with valid data`() = runTest {
        val importRequest = JointAccountDto(
            creationDatetime = null,
            address = TEST_ADDRESS,
            version = 1,
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS
        )
        val inboxMessages = mockk<InboxMessages> {
            every { jointAccountImportRequests } returns listOf(importRequest)
        }
        coEvery { getInboxMessages() } returns inboxMessages

        val result = sut.fetchInvitationFromInbox(TEST_ADDRESS)

        assertTrue(result is JointAccountDetailProcessor.InvitationResult.Success)
        val successResult = result as JointAccountDetailProcessor.InvitationResult.Success
        assertEquals(DEFAULT_THRESHOLD, successResult.data.threshold)
        assertEquals(DEFAULT_PARTICIPANTS, successResult.data.participantAddresses)
    }

    // endregion

    // region deleteInboxNotification Tests

    @Test
    fun `EXPECT no action WHEN device id is null`() = runTest {
        coEvery { getSelectedNodeDeviceId() } returns null

        sut.deleteInboxNotification(TEST_ADDRESS)

        coVerify(exactly = 0) { inboxApiRepository.deleteJointInvitationNotification(any(), any()) }
    }

    @Test
    fun `EXPECT no action WHEN device id is not a valid number`() = runTest {
        coEvery { getSelectedNodeDeviceId() } returns "invalid"

        sut.deleteInboxNotification(TEST_ADDRESS)

        coVerify(exactly = 0) { inboxApiRepository.deleteJointInvitationNotification(any(), any()) }
    }

    @Test
    fun `EXPECT deleteJointInvitationNotification called WHEN device id is valid`() = runTest {
        coEvery { getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { inboxApiRepository.deleteJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS) } returns PeraResult.Success(Unit)

        sut.deleteInboxNotification(TEST_ADDRESS)

        coVerify { inboxApiRepository.deleteJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS) }
    }

    // endregion

    // region isJointAccountExists Tests

    @Test
    fun `EXPECT true WHEN joint account exists`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns createJointAccount()

        val result = sut.isJointAccountExists(TEST_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN joint account does not exist`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null

        val result = sut.isJointAccountExists(TEST_ADDRESS)

        assertFalse(result)
    }

    // endregion

    // region getContactEditInfo Tests

    @Test
    fun `EXPECT null WHEN contact not found`() = runTest {
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns null

        val result = sut.getContactEditInfo(TEST_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT contact info WHEN contact found`() = runTest {
        val contact = mockk<User> {
            every { name } returns "Test Contact"
            every { publicKey } returns TEST_ADDRESS
            every { contactDatabaseId } returns 1
            every { imageUriAsString } returns "test_uri"
        }
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns contact

        val result = sut.getContactEditInfo(TEST_ADDRESS)

        assertEquals("Test Contact", result?.contactName)
        assertEquals(TEST_ADDRESS, result?.contactPublicKey)
        assertEquals(1, result?.contactDatabaseId)
        assertEquals("test_uri", result?.contactProfileImageUri)
    }

    // endregion

    // region createParticipantItems Tests

    @Test
    fun `EXPECT participant items created for all addresses`() = runTest {
        coEvery { createJointAccountParticipantItem("ADDR1") } returns createParticipantItem("ADDR1")
        coEvery { createJointAccountParticipantItem("ADDR2") } returns createParticipantItem("ADDR2")
        coEvery { createJointAccountParticipantItem("ADDR3") } returns createParticipantItem("ADDR3")

        val result = sut.createParticipantItems(DEFAULT_PARTICIPANTS)

        assertEquals(3, result.size)
        coVerify(exactly = 3) { createJointAccountParticipantItem(any()) }
    }

    // endregion

    // region Helpers

    private fun createJointAccount() = LocalAccount.Joint(
        algoAddress = TEST_ADDRESS,
        participantAddresses = DEFAULT_PARTICIPANTS,
        threshold = DEFAULT_THRESHOLD,
        version = 1
    )

    private fun createParticipantItem(address: String) = JointAccountParticipantItem(
        address = address,
        displayName = address,
        secondaryDisplayName = "${address.take(4)}...${address.takeLast(4)}",
        iconDrawablePreview = mockk<AccountIconDrawablePreview>(),
        imageUri = null,
        isLocalAccount = false,
        isContact = false
    )

    // endregion

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_DEVICE_ID = "12345"
        const val TEST_DEVICE_ID_LONG = 12345L
        const val DEFAULT_THRESHOLD = 2
        val DEFAULT_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
    }
}
