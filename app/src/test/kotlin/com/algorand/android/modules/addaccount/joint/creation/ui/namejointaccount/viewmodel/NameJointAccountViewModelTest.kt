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

import com.algorand.android.R
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.CreateJointAccount
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewEvent
import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountViewModel.ViewState
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetDefaultJointAccountName
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class NameJointAccountViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val createJointAccount: CreateJointAccount = mockk()
    private val getDefaultJointAccountName: GetDefaultJointAccountName = mockk()
    private val processor: NameJointAccountProcessor = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `EXPECT default account name WHEN getDefaultAccountName called`() = runTest {
        coEvery { getDefaultJointAccountName() } returns TEST_DEFAULT_NAME

        val viewModel = createViewModel()
        val result = viewModel.getDefaultAccountName()

        assertEquals(TEST_DEFAULT_NAME, result)
    }

    @Test
    fun `EXPECT Error state WHEN account name is blank`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount("   ", TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Error)
        assertEquals(R.string.an_error_occurred, (state as ViewState.Error).messageResId)
    }

    @Test
    fun `EXPECT Success state WHEN createJointAccount completes successfully`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO())
        coEvery {
            processor.createLocalAccount(any(), any(), any(), any(), any())
        } returns NameJointAccountProcessor.CreateLocalAccountResult.Success

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Success)
    }

    @Test
    fun `EXPECT Success state and event WHEN joint account created successfully`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO())
        coEvery {
            processor.createLocalAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME)
        } returns NameJointAccountProcessor.CreateLocalAccountResult.Success

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)
        val events = mutableListOf<ViewEvent>()
        val job = launch { eventDelegate.viewEvent.toList(events) }

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()
        job.cancel()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Success)
        assertTrue(events.any { it is ViewEvent.AccountCreatedSuccessfully })
    }

    @Test
    fun `EXPECT Error state WHEN createJointAccount API fails`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        val exception = Exception("Network error")
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Error(exception)
        coEvery { processor.mapExceptionToErrorResId(exception) } returns R.string.the_internet_connection

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Error)
        assertEquals(R.string.the_internet_connection, (state as ViewState.Error).messageResId)
    }

    @Test
    fun `EXPECT Error state WHEN joint account address is null`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO(address = null))

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Error)
        assertEquals(R.string.an_error_occurred, (state as ViewState.Error).messageResId)
    }

    @Test
    fun `EXPECT Error state WHEN account already exists locally`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO())
        coEvery {
            processor.createLocalAccount(any(), any(), any(), any(), any())
        } returns NameJointAccountProcessor.CreateLocalAccountResult.AlreadyExists

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Error)
        assertEquals(R.string.this_account_already_exists, (state as ViewState.Error).messageResId)
    }

    @Test
    fun `EXPECT Error state WHEN local account creation fails`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO())
        coEvery {
            processor.createLocalAccount(any(), any(), any(), any(), any())
        } returns NameJointAccountProcessor.CreateLocalAccountResult.Error(R.string.an_error_occurred)

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount(TEST_ACCOUNT_NAME, TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Error)
    }

    @Test
    fun `EXPECT account name trimmed WHEN creating joint account`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()
        coEvery {
            createJointAccount(TEST_PARTICIPANTS, TEST_THRESHOLD, any())
        } returns Result.Success(createJointAccountDTO())
        coEvery {
            processor.createLocalAccount(TEST_JOINT_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_ACCOUNT_NAME)
        } returns NameJointAccountProcessor.CreateLocalAccountResult.Success

        val viewModel = createViewModelWithDelegates(stateDelegate, eventDelegate)

        viewModel.createJointAccount("  $TEST_ACCOUNT_NAME  ", TEST_THRESHOLD, TEST_PARTICIPANTS)
        advanceUntilIdle()

        coVerify {
            processor.createLocalAccount(any(), any(), any(), any(), TEST_ACCOUNT_NAME)
        }
    }

    @Test
    fun `EXPECT Idle state initially`() = runTest {
        val stateDelegate = StateDelegate<ViewState>()
        val eventDelegate = EventDelegate<ViewEvent>()

        createViewModelWithDelegates(stateDelegate, eventDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is ViewState.Idle)
    }

    private fun createViewModel(): NameJointAccountViewModel {
        return NameJointAccountViewModel(
            stateDelegate = StateDelegate(),
            eventDelegate = EventDelegate(),
            createJointAccount = createJointAccount,
            getDefaultJointAccountName = getDefaultJointAccountName,
            processor = processor
        )
    }

    private fun createViewModelWithDelegates(
        stateDelegate: StateDelegate<ViewState>,
        eventDelegate: EventDelegate<ViewEvent>
    ): NameJointAccountViewModel {
        return NameJointAccountViewModel(
            stateDelegate = stateDelegate,
            eventDelegate = eventDelegate,
            createJointAccount = createJointAccount,
            getDefaultJointAccountName = getDefaultJointAccountName,
            processor = processor
        )
    }

    private fun createJointAccountDTO(
        address: String? = TEST_JOINT_ADDRESS,
        version: Int = TEST_VERSION
    ): JointAccountDTO {
        return JointAccountDTO(
            creationDatetime = "2025-01-01T00:00:00Z",
            address = address,
            version = version,
            threshold = TEST_THRESHOLD,
            participantAddresses = TEST_PARTICIPANTS
        )
    }

    private companion object {
        const val TEST_DEFAULT_NAME = "Joint Account 1"
        const val TEST_ACCOUNT_NAME = "My Joint Account"
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
        val TEST_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
    }
}
