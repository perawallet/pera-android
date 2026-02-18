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

package com.algorand.android.modules.addaccount.joint.transaction.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequest
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionViewState
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
internal class JointAccountTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getJointAccountTransactionViewState: GetJointAccountTransactionViewState = mockk()
    private val declineJointAccountSignRequest: DeclineJointAccountSignRequest = mockk()
    private val signAndSubmitJointAccountSignature: SignAndSubmitJointAccountSignature = mockk()
    private val refreshInboxCache: RefreshInboxCache = mockk(relaxed = true)
    private val getInboxMessagesFlow: GetInboxMessagesFlow = mockk(relaxed = true)
    private val processor: JointAccountTransactionProcessor = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `EXPECT Loading state initially`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createTestPreview()
        )
        every { processor.processLoadedPreview(any()) } answers { firstArg() }

        createViewModel(stateDelegate, eventDelegate)

        assertTrue(stateDelegate.state.value is JointAccountTransactionViewModel.ViewState.Loading)
    }

    @Test
    fun `EXPECT Content state WHEN preview loaded successfully`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        val preview = createTestPreview()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(preview)
        every { processor.processLoadedPreview(preview) } returns preview

        createViewModel(stateDelegate, eventDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is JointAccountTransactionViewModel.ViewState.Content)
        assertEquals(preview, (state as JointAccountTransactionViewModel.ViewState.Content).preview)
    }

    @Test
    fun `EXPECT Error state WHEN preview load fails`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Error(Exception("Error"))

        createViewModel(stateDelegate, eventDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is JointAccountTransactionViewModel.ViewState.Error)
    }

    @Test
    fun `EXPECT Canceled state WHEN onCancel called`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        val preview = createTestPreview()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(preview)
        every { processor.processLoadedPreview(preview) } returns preview

        val viewModel = createViewModel(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.onCancel()
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is JointAccountTransactionViewModel.ViewState.Content)
        assertEquals(
            JointAccountTransactionState.Canceled,
            (state as JointAccountTransactionViewModel.ViewState.Content).preview.transactionState
        )
    }

    @Test
    fun `EXPECT decline called WHEN declineSignRequest invoked`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        val preview = createTestPreview()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(preview)
        every { processor.processLoadedPreview(preview) } returns preview
        every { processor.findDeclineParticipantAddresses(preview) } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery {
            declineJointAccountSignRequest(
                TEST_SIGN_REQUEST_ID,
                listOf(TEST_PARTICIPANT_ADDRESS)
            )
        } returns PeraResult.Success(mockk())

        val viewModel = createViewModel(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.declineSignRequest()
        advanceUntilIdle()

        coVerify { declineJointAccountSignRequest(TEST_SIGN_REQUEST_ID, listOf(TEST_PARTICIPANT_ADDRESS)) }
    }

    @Test
    fun `EXPECT refresh inbox WHEN ledger sign succeeds`() = runTest {
        val stateDelegate = StateDelegate<JointAccountTransactionViewModel.ViewState>()
        val eventDelegate = EventDelegate<JointAccountTransactionViewModel.ViewEvent>()
        val preview = createTestPreview()
        coEvery { getJointAccountTransactionViewState(TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(preview)
        every { processor.processLoadedPreview(preview) } returns preview
        every {
            processor.determineLedgerSuccessAction(
                preview,
                TEST_SIGN_REQUEST_ID
            )
        } returns JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures

        val viewModel = createViewModel(stateDelegate, eventDelegate)
        advanceUntilIdle()

        viewModel.onLedgerSignSuccess()
        advanceUntilIdle()

        coVerify { refreshInboxCache() }
    }

    private fun createViewModel(
        stateDelegate: StateDelegate<JointAccountTransactionViewModel.ViewState>,
        eventDelegate: EventDelegate<JointAccountTransactionViewModel.ViewEvent>
    ): JointAccountTransactionViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("signRequestId" to TEST_SIGN_REQUEST_ID))
        return JointAccountTransactionViewModel(
            savedStateHandle = savedStateHandle,
            stateDelegate = stateDelegate,
            eventDelegate = eventDelegate,
            getJointAccountTransactionViewState = getJointAccountTransactionViewState,
            declineJointAccountSignRequest = declineJointAccountSignRequest,
            signAndSubmitJointAccountSignature = signAndSubmitJointAccountSignature,
            refreshInboxCache = refreshInboxCache,
            getInboxMessagesFlow = getInboxMessagesFlow,
            processor = processor
        )
    }

    private fun createTestPreview(): JointAccountTransactionViewState {
        return JointAccountTransactionViewState(
            jointAccountDisplayName = AccountDisplayName(
                accountAddress = TEST_JOINT_ADDRESS,
                primaryDisplayName = "Joint Account",
                secondaryDisplayName = null
            ),
            jointAccountIconPreview = mockk<AccountIconDrawablePreview>(relaxed = true),
            recipientAddress = "RECIPIENT_ADDRESS",
            recipientShortAddress = "RECIP...ADDR",
            amount = "10.00 ALGO",
            convertedAmount = "$10.00",
            transactionFee = "-0.001 ALGO",
            transactionState = JointAccountTransactionState.AwaitingConfirmation,
            signerAccounts = emptyList(),
            signedCount = 1,
            requiredSignatureCount = 2,
            hasCurrentUserAlreadySigned = false,
            shouldShowPendingSignaturesDirectly = false,
            rawTransactions = listOf("raw_tx"),
            unsignedLocalParticipantAddresses = listOf(TEST_PARTICIPANT_ADDRESS),
            unsignedLedgerParticipantAddresses = emptyList()
        )
    }

    private companion object {
        const val TEST_SIGN_REQUEST_ID = "sign_request_123"
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PARTICIPANT_ADDRESS = "PARTICIPANT_ADDRESS"
    }
}
