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

package com.algorand.android.core.transaction.sync

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncSignRequestPollingManagerTest {

    private val getSyncSignRequestWithSignatures = mockk<GetSyncSignRequestWithSignatures>()
    private lateinit var pollingManager: SyncSignRequestPollingManager
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        pollingManager = SyncSignRequestPollingManager(getSyncSignRequestWithSignatures)
    }

    @Test
    fun `EXPECT Idle state WHEN not started`() {
        assertEquals(SyncSignRequestPollingState.Idle, pollingManager.stateFlow.value)
    }

    @Test
    fun `EXPECT SignaturesReady WHEN status is READY`() = testScope.runTest {
        val signRequest = createSignRequest(SignRequestStatus.READY)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        val state = pollingManager.stateFlow.value
        assertTrue(state is SyncSignRequestPollingState.SignaturesReady)
    }

    @Test
    fun `EXPECT SignaturesReady WHEN status is CONFIRMED`() = testScope.runTest {
        val signRequest = createSignRequest(SignRequestStatus.CONFIRMED)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        val state = pollingManager.stateFlow.value
        assertTrue(state is SyncSignRequestPollingState.SignaturesReady)
    }

    @Test
    fun `EXPECT Declined WHEN status is DECLINED`() = testScope.runTest {
        val signRequest = createSignRequest(SignRequestStatus.DECLINED)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        assertEquals(SyncSignRequestPollingState.Declined, pollingManager.stateFlow.value)
    }

    @Test
    fun `EXPECT Expired WHEN status is EXPIRED`() = testScope.runTest {
        val signRequest = createSignRequest(SignRequestStatus.EXPIRED)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        assertEquals(SyncSignRequestPollingState.Expired, pollingManager.stateFlow.value)
    }

    @Test
    fun `EXPECT Failed with NetworkError WHEN result is error`() = testScope.runTest {
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Error(Exception("network error"))

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        val state = pollingManager.stateFlow.value
        assertTrue(state is SyncSignRequestPollingState.Failed)
        assertEquals(
            SyncSignRequestFailReason.NetworkError,
            (state as SyncSignRequestPollingState.Failed).reason
        )
    }

    @Test
    fun `EXPECT Failed with SignRequestNotFound WHEN status is null`() = testScope.runTest {
        val signRequest = createSignRequest(null)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()

        val state = pollingManager.stateFlow.value
        assertTrue(state is SyncSignRequestPollingState.Failed)
        assertEquals(
            SyncSignRequestFailReason.SignRequestNotFound,
            (state as SyncSignRequestPollingState.Failed).reason
        )
    }

    @Test
    fun `EXPECT Idle WHEN stopPolling called during polling`() = testScope.runTest {
        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        pollingManager.stopPolling()

        assertEquals(SyncSignRequestPollingState.Idle, pollingManager.stateFlow.value)
    }

    @Test
    fun `EXPECT Idle WHEN resetState called`() = testScope.runTest {
        val signRequest = createSignRequest(SignRequestStatus.READY)
        coEvery {
            getSyncSignRequestWithSignatures(any(), any())
        } returns PeraResult.Success(signRequest)

        pollingManager.startPolling(testScope, TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)
        advanceUntilIdle()
        pollingManager.resetState()

        assertEquals(SyncSignRequestPollingState.Idle, pollingManager.stateFlow.value)
    }

    private fun createSignRequest(status: SignRequestStatus?): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = TEST_SIGN_REQUEST_ID,
            type = null,
            jointAccount = null,
            proposerAddress = null,
            lastValidExpectedDatetime = null,
            transactionLists = null,
            status = status,
            failReasonDisplay = null
        )
    }

    private companion object {
        const val TEST_DEVICE_ID = "test_device_123"
        const val TEST_SIGN_REQUEST_ID = "test_sign_request_456"
    }
}
