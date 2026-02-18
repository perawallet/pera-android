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

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ErrorType
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ViewEvent
import com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel.JointAccountDetailViewModel.ViewState
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class JointAccountDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getJointAccount: GetJointAccount = mockk()
    private val processor: JointAccountDetailProcessor = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region Initialization Tests

    @Test
    fun `EXPECT showActions false WHEN local account exists without invitation data`() = runTest {
        val jointAccount = createJointAccount()
        setupLocalAccountMocks(jointAccount, showActions = false)
        val savedStateHandle = createSavedStateHandle()

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        assertFalse(state.showActions)
        assertEquals(DEFAULT_PARTICIPANT_COUNT, state.numberOfAccounts)
        assertEquals(DEFAULT_THRESHOLD, state.threshold)
    }

    @Test
    fun `EXPECT showActions true WHEN invitation data provided without local account`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery {
            processor.createContentStateFromInvitation(DEFAULT_PARTICIPANTS, DEFAULT_THRESHOLD, TEST_ADDRESS)
        } returns createContentState(showActions = true)

        val savedStateHandle = createSavedStateHandle(
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS.toTypedArray()
        )

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        assertTrue(state.showActions)
    }

    @Test
    fun `EXPECT showActions true WHEN invitation data provided with existing local account`() = runTest {
        val jointAccount = createJointAccount()
        coEvery { getJointAccount(TEST_ADDRESS) } returns jointAccount
        coEvery {
            processor.createContentState(jointAccount, TEST_ADDRESS, showActions = true)
        } returns createContentState(showActions = true)

        val savedStateHandle = createSavedStateHandle(
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS.toTypedArray()
        )

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        assertTrue(state.showActions)
    }

    @Test
    fun `EXPECT participants exposed via Content state`() = runTest {
        val jointAccount = createJointAccount()
        setupLocalAccountMocks(jointAccount, showActions = false)
        val savedStateHandle = createSavedStateHandle()

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        val participantAddresses = state.participants.map { it.address }
        assertEquals(DEFAULT_PARTICIPANTS, participantAddresses)
    }

    // endregion

    // region Error State Tests

    @Test
    fun `EXPECT Error INVITATION_NOT_FOUND WHEN inbox and api both return NotFound`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { processor.fetchInvitationFromInbox(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NotFound
        coEvery { processor.fetchJointAccountFromApi(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NotFound
        val savedStateHandle = createSavedStateHandle()

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Error
        assertEquals(ErrorType.INVITATION_NOT_FOUND, state.type)
    }

    @Test
    fun `EXPECT Error NETWORK_ERROR WHEN inbox and api both fail`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { processor.fetchInvitationFromInbox(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NetworkError
        coEvery { processor.fetchJointAccountFromApi(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NetworkError
        val savedStateHandle = createSavedStateHandle()

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Error
        assertEquals(ErrorType.NETWORK_ERROR, state.type)
    }

    @Test
    fun `EXPECT Content WHEN inbox returns NotFound but api returns Success`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { processor.fetchInvitationFromInbox(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NotFound
        coEvery { processor.fetchJointAccountFromApi(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.Success(
                    JointAccountDetailProcessor.InvitationData(
                        threshold = DEFAULT_THRESHOLD,
                        participantAddresses = DEFAULT_PARTICIPANTS
                    )
                )
        coEvery {
            processor.createContentStateFromInvitation(DEFAULT_PARTICIPANTS, DEFAULT_THRESHOLD, TEST_ADDRESS)
        } returns createContentState(showActions = true)
        val savedStateHandle = createSavedStateHandle()

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        assertTrue(state.showActions)
        assertEquals(DEFAULT_THRESHOLD, state.threshold)
    }

    @Test
    fun `EXPECT createContentStateFromInvitation not called WHEN both inbox and api fail`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { processor.fetchInvitationFromInbox(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NotFound
        coEvery { processor.fetchJointAccountFromApi(TEST_ADDRESS) } returns
                JointAccountDetailProcessor.InvitationResult.NotFound
        val savedStateHandle = createSavedStateHandle()

        createViewModel(savedStateHandle)
        advanceUntilIdle()

        coVerify(exactly = 0) { processor.createContentStateFromInvitation(any(), any(), any()) }
    }

    // endregion

    // region onIgnoreClick Tests

    @Test
    fun `EXPECT InvitationIgnored event WHEN onIgnoreClick called`() = runTest {
        val jointAccount = createJointAccount()
        setupLocalAccountMocks(jointAccount, showActions = false)
        coEvery { processor.deleteInboxNotification(TEST_ADDRESS) } returns Unit
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(createSavedStateHandle(), eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onIgnoreClick()
        advanceUntilIdle()
        job.cancel()

        coVerify { processor.deleteInboxNotification(TEST_ADDRESS) }
        assertTrue(events.contains(ViewEvent.InvitationIgnored))
    }

    // endregion

    // region onAddClick Tests

    @Test
    fun `EXPECT NavigateToNameJointAccount WHEN onAddClick and account does not exist`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery {
            processor.createContentStateFromInvitation(DEFAULT_PARTICIPANTS, DEFAULT_THRESHOLD, TEST_ADDRESS)
        } returns createContentState(showActions = true)
        coEvery { processor.deleteInboxNotification(TEST_ADDRESS) } returns Unit
        coEvery { processor.isJointAccountExists(TEST_ADDRESS) } returns false

        val savedStateHandle = createSavedStateHandle(
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS.toTypedArray()
        )
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(savedStateHandle, eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onAddClick()
        advanceUntilIdle()
        job.cancel()

        coVerify { processor.deleteInboxNotification(TEST_ADDRESS) }
        coVerify { processor.isJointAccountExists(TEST_ADDRESS) }

        val navEvent = events.filterIsInstance<ViewEvent.NavigateToNameJointAccount>().firstOrNull()
        assertEquals(DEFAULT_THRESHOLD, navEvent?.threshold)
        assertEquals(DEFAULT_PARTICIPANTS, navEvent?.participantAddresses)
    }

    @Test
    fun `EXPECT NavigateBack WHEN onAddClick and account already exists`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery {
            processor.createContentStateFromInvitation(DEFAULT_PARTICIPANTS, DEFAULT_THRESHOLD, TEST_ADDRESS)
        } returns createContentState(showActions = true)
        coEvery { processor.deleteInboxNotification(TEST_ADDRESS) } returns Unit
        coEvery { processor.isJointAccountExists(TEST_ADDRESS) } returns true

        val savedStateHandle = createSavedStateHandle(
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS.toTypedArray()
        )
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(savedStateHandle, eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onAddClick()
        advanceUntilIdle()
        job.cancel()

        assertTrue(events.contains(ViewEvent.NavigateBack))
    }

    @Test
    fun `EXPECT no action WHEN onAddClick with zero threshold`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery {
            processor.createContentStateFromInvitation(DEFAULT_PARTICIPANTS, DEFAULT_THRESHOLD, TEST_ADDRESS)
        } returns createContentState(threshold = 0, showActions = true)

        val savedStateHandle = createSavedStateHandle(
            threshold = DEFAULT_THRESHOLD,
            participantAddresses = DEFAULT_PARTICIPANTS.toTypedArray()
        )
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(savedStateHandle, eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onAddClick()
        advanceUntilIdle()
        job.cancel()

        coVerify(exactly = 0) { processor.deleteInboxNotification(any()) }
        assertTrue(events.isEmpty())
    }

    @Test
    fun `EXPECT no action WHEN onAddClick with empty participants`() = runTest {
        val jointAccount = createJointAccount()
        coEvery { getJointAccount(TEST_ADDRESS) } returns jointAccount
        coEvery {
            processor.createContentState(jointAccount, TEST_ADDRESS, false)
        } returns createContentState(participantAddresses = emptyList(), showActions = false)

        val savedStateHandle = createSavedStateHandle()
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(savedStateHandle, eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onAddClick()
        advanceUntilIdle()
        job.cancel()

        coVerify(exactly = 0) { processor.deleteInboxNotification(any()) }
        assertTrue(events.isEmpty())
    }

    // endregion

    // region Action Tests

    @Test
    fun `EXPECT updated participants WHEN refreshParticipants called`() = runTest {
        val jointAccount = createJointAccount()
        setupLocalAccountMocks(jointAccount, showActions = false)
        val updatedParticipants = listOf(mockk<JointAccountParticipantItem>())
        coEvery { processor.createParticipantItems(DEFAULT_PARTICIPANTS) } returns updatedParticipants

        val viewModel = createViewModel(createSavedStateHandle())
        advanceUntilIdle()

        viewModel.refreshParticipants()
        advanceUntilIdle()

        val state = viewModel.state.value as ViewState.Content
        assertEquals(updatedParticipants, state.participants)
    }

    @Test
    fun `EXPECT NavigateToEditAddress WHEN onEditParticipantClick called`() = runTest {
        val jointAccount = createJointAccount()
        setupLocalAccountMocks(jointAccount, showActions = false)
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithEventDelegate(createSavedStateHandle(), eventDelegate)
        advanceUntilIdle()

        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onEditParticipantClick(TEST_ADDRESS)
        advanceUntilIdle()
        job.cancel()

        val event = events.filterIsInstance<ViewEvent.NavigateToEditAddress>().firstOrNull()
        assertNotNull(event)
        assertEquals(TEST_ADDRESS, event?.address)
    }

    // endregion

    // region Helpers

    private fun createViewModel(savedStateHandle: SavedStateHandle): JointAccountDetailViewModel {
        return JointAccountDetailViewModel(
            savedStateHandle = savedStateHandle,
            stateDelegate = StateDelegate(),
            eventDelegate = EventDelegate(),
            getJointAccount = getJointAccount,
            processor = processor
        )
    }

    private fun createViewModelWithEventDelegate(
        savedStateHandle: SavedStateHandle,
        eventDelegate: EventDelegate<ViewEvent>
    ): JointAccountDetailViewModel {
        return JointAccountDetailViewModel(
            savedStateHandle = savedStateHandle,
            stateDelegate = StateDelegate(),
            eventDelegate = eventDelegate,
            getJointAccount = getJointAccount,
            processor = processor
        )
    }

    private fun createSavedStateHandle(
        accountAddress: String = TEST_ADDRESS,
        threshold: Int = 0,
        participantAddresses: Array<String>? = null
    ): SavedStateHandle {
        val map = mutableMapOf<String, Any>(JointAccountDetailViewModel.ACCOUNT_ADDRESS_KEY to accountAddress)
        if (threshold > 0) map[JointAccountDetailViewModel.THRESHOLD_KEY] = threshold
        if (participantAddresses != null) map[JointAccountDetailViewModel.PARTICIPANT_ADDRESSES_KEY] =
            participantAddresses
        return SavedStateHandle(map)
    }

    private fun createJointAccount() = LocalAccount.Joint(
        algoAddress = TEST_ADDRESS,
        participantAddresses = DEFAULT_PARTICIPANTS,
        threshold = DEFAULT_THRESHOLD,
        version = 1
    )

    private fun createContentState(
        participantAddresses: List<String> = DEFAULT_PARTICIPANTS,
        threshold: Int = DEFAULT_THRESHOLD,
        showActions: Boolean = false
    ) = ViewState.Content(
        accountDisplayName = "Joint Account",
        accountAddressShortened = "JOINT...123",
        numberOfAccounts = participantAddresses.size,
        threshold = threshold,
        participants = participantAddresses.map { createParticipantItem(it) },
        showActions = showActions
    )

    private fun createParticipantItem(address: String) = JointAccountParticipantItem(
        address = address,
        displayName = address,
        secondaryDisplayName = "${address.take(4)}...${address.takeLast(4)}",
        iconDrawablePreview = mockk(),
        imageUri = null,
        isLocalAccount = false,
        isContact = false
    )

    private fun setupLocalAccountMocks(jointAccount: LocalAccount.Joint, showActions: Boolean) {
        coEvery { getJointAccount(TEST_ADDRESS) } returns jointAccount
        coEvery {
            processor.createContentState(jointAccount, TEST_ADDRESS, showActions)
        } returns createContentState(showActions = showActions)
    }

    // endregion

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        const val DEFAULT_THRESHOLD = 2
        val DEFAULT_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
        const val DEFAULT_PARTICIPANT_COUNT = 3
    }
}
