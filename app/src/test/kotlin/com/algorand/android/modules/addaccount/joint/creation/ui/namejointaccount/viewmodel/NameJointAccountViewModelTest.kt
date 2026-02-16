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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewEvent
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewState
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetNextJointAccountNumber
import com.algorand.android.ui.device.model.DeviceConfig
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.wallet.account.core.domain.usecase.AddJointAccount
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class NameJointAccountViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val createJointAccount: CreateJointAccount = mockk()
    private val getNextJointAccountNumber: GetNextJointAccountNumber = mockk()
    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes = mockk()
    private val addJointAccount: AddJointAccount = mockk()
    private val getJointAccount: GetJointAccount = mockk()
    private val getDeviceConfig: GetDeviceConfig = mockk()
    private val deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(android.util.Log::class)
        io.mockk.every { android.util.Log.e(any(), any(), any()) } returns 0
        coEvery { getNextJointAccountNumber() } returns DEFAULT_JOINT_ACCOUNT_NUMBER
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `EXPECT default joint account number in Idle state WHEN init completes`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertEquals(DEFAULT_JOINT_ACCOUNT_NUMBER, (state as ViewState.Idle).defaultJointAccountNumber)
    }

    @Test
    fun `EXPECT ShowError event WHEN account name is blank`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }
        advanceUntilIdle()

        viewModel.onAccountNameChanged("   ")
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertTrue(events.any { it is ViewEvent.ShowError && it.messageResId == R.string.an_error_occurred })
    }

    @Test
    fun `EXPECT Success state WHEN joint account created successfully`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 0)
        } returns PeraResult.Success(Unit)
        coEvery { getDeviceConfig() } returns createDeviceConfig()
        coEvery {
            deleteInboxJointInvitationNotification(
                TEST_DEVICE_ID,
                TEST_JOINT_ADDRESS
            )
        } returns PeraResult.Success(Unit)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Success)
    }

    @Test
    fun `EXPECT Success state and event WHEN joint account created successfully`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 0)
        } returns PeraResult.Success(Unit)
        coEvery { getDeviceConfig() } returns createDeviceConfig()
        coEvery {
            deleteInboxJointInvitationNotification(
                TEST_DEVICE_ID,
                TEST_JOINT_ADDRESS
            )
        } returns PeraResult.Success(Unit)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Success)
        assertTrue(events.any { it is ViewEvent.AccountCreatedSuccessfully })
    }

    @Test
    fun `EXPECT ShowError event WHEN createJointAccount API fails`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        val exception = Exception("Network error")
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Error(exception)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertTrue(events.any { it is ViewEvent.ShowError })
    }

    @Test
    fun `EXPECT ShowError event WHEN joint account address is null`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto(address = null))

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertTrue(events.any { it is ViewEvent.ShowError && it.messageResId == R.string.an_error_occurred })
    }

    @Test
    fun `EXPECT ShowError event WHEN account already exists locally`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns createLocalAccountJoint()
        coEvery { getDeviceConfig() } returns createDeviceConfig()
        coEvery {
            deleteInboxJointInvitationNotification(
                TEST_DEVICE_ID,
                TEST_JOINT_ADDRESS
            )
        } returns PeraResult.Success(Unit)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertTrue(events.any { it is ViewEvent.ShowError && it.messageResId == R.string.this_account_already_exists })
    }

    @Test
    fun `EXPECT ShowError event WHEN local account creation fails`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 0)
        } returns PeraResult.Error(Exception("DB error"))

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertTrue(events.any { it is ViewEvent.ShowError })
    }

    @Test
    fun `EXPECT correct order index WHEN creating joint account`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns listOf(
            AccountOrderIndex("ADDR1", 0),
            AccountOrderIndex("ADDR2", 5),
            AccountOrderIndex("ADDR3", 3)
        )
        coEvery {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 6)
        } returns PeraResult.Success(Unit)
        coEvery { getDeviceConfig() } returns createDeviceConfig()
        coEvery {
            deleteInboxJointInvitationNotification(
                TEST_DEVICE_ID,
                TEST_JOINT_ADDRESS
            )
        } returns PeraResult.Success(Unit)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        advanceUntilIdle()

        coVerify {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 6)
        }
    }

    @Test
    fun `EXPECT no state change WHEN onFinishClick called during Loading`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION)
        } returns PeraResult.Success(createJointAccountDto())
        coEvery { getJointAccount(TEST_JOINT_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME, 0)
        } returns PeraResult.Success(Unit)
        coEvery { getDeviceConfig() } returns createDeviceConfig()
        coEvery {
            deleteInboxJointInvitationNotification(
                TEST_DEVICE_ID,
                TEST_JOINT_ADDRESS
            )
        } returns PeraResult.Success(Unit)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.onAccountNameChanged(TEST_ACCOUNT_NAME)
        viewModel.onFinishClick()
        // Call again immediately — should be ignored because state is Loading
        viewModel.onFinishClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION) }
    }

    @Test
    fun `EXPECT Idle state with empty account name WHEN init completes`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()

        createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
        assertEquals("", (state as ViewState.Idle).accountName)
    }

    private fun createViewModelWithDelegates(
        stateDelegate: StateDelegate<ViewState>,
        eventDelegate: EventDelegate<ViewEvent>
    ): NameJointAccountViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "threshold" to TEST_THRESHOLD,
                "participantAddresses" to TEST_PARTICIPANTS.toTypedArray()
            )
        )
        return NameJointAccountViewModel(
            savedStateHandle = savedStateHandle,
            stateDelegate = stateDelegate,
            eventDelegate = eventDelegate,
            createJointAccount = createJointAccount,
            getNextJointAccountNumber = getNextJointAccountNumber,
            getAllAccountOrderIndexes = getAllAccountOrderIndexes,
            addJointAccount = addJointAccount,
            getJointAccount = getJointAccount,
            inboxCleanup = NameJointAccountInboxCleanup(getDeviceConfig, deleteInboxJointInvitationNotification)
        )
    }

    private fun createJointAccountDto(
        address: String? = TEST_JOINT_ADDRESS,
        version: Int = TEST_VERSION
    ): JointAccount {
        return JointAccount(
            creationDatetime = "2025-01-01T00:00:00Z",
            address = address,
            version = version,
            threshold = TEST_THRESHOLD,
            participantAddresses = TEST_PARTICIPANTS
        )
    }

    private fun createLocalAccountJoint(): LocalAccount.Joint = LocalAccount.Joint(
        algoAddress = TEST_JOINT_ADDRESS,
        participantAddresses = TEST_PARTICIPANTS,
        threshold = TEST_THRESHOLD,
        version = TEST_VERSION
    )

    private fun createDeviceConfig(): DeviceConfig = DeviceConfig(
        platform = "android",
        appName = "pera",
        appPackageName = "com.algorand.android",
        appVersion = "1.0.0",
        deviceId = TEST_DEVICE_ID_STRING,
        deviceVersion = "1",
        deviceOSVersion = "14",
        deviceModel = "Pixel",
        theme = mockk(),
        node = mockk(),
        currency = "USD",
        region = "US",
        language = "en"
    )

    private companion object {
        const val TEST_ACCOUNT_NAME = "My Joint Account"
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
        const val DEFAULT_JOINT_ACCOUNT_NUMBER = 1
        const val TEST_DEVICE_ID_STRING = "12345"
        const val TEST_DEVICE_ID = 12345L
        val TEST_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
    }
}
