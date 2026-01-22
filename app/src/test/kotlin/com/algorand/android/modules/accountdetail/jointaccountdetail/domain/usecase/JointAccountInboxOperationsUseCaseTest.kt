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

package com.algorand.android.modules.accountdetail.jointaccountdetail.domain.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.inbox.domain.usecase.FetchInboxMessages
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

internal class JointAccountInboxOperationsUseCaseTest {

    private val deviceIdUseCase: DeviceIdUseCase = mockk()
    private val fetchInboxMessages: FetchInboxMessages = mockk()
    private val deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification = mockk()

    private val sut = JointAccountInboxOperationsUseCase(
        deviceIdUseCase = deviceIdUseCase,
        fetchInboxMessages = fetchInboxMessages,
        deleteInboxJointInvitationNotification = deleteInboxJointInvitationNotification
    )

    @Test
    fun `EXPECT device id WHEN getDeviceId called`() {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID

        val result = sut.getDeviceId()

        assertEquals(TEST_DEVICE_ID, result)
    }

    @Test
    fun `EXPECT null WHEN device id not available`() {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        val result = sut.getDeviceId()

        assertNull(result)
    }

    @Test
    fun `EXPECT error WHEN device id not available for getInboxMessages`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        val result = sut.getInboxMessages(listOf("ADDR1"))

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT success WHEN getInboxMessages succeeds`() = runTest {
        val expectedMessages = mockk<InboxMessages>()
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { fetchInboxMessages(TEST_DEVICE_ID_LONG, listOf("ADDR1")) } returns PeraResult.Success(expectedMessages)

        val result = sut.getInboxMessages(listOf("ADDR1"))

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedMessages, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT deleteNotification to call deleteInboxJointInvitationNotification`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { deleteInboxJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS) } returns PeraResult.Success(Unit)

        val result = sut.deleteNotification(TEST_ADDRESS)

        assertTrue(result)
        coVerify { deleteInboxJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS) }
    }

    @Test
    fun `EXPECT false WHEN device id is null for deleteNotification`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        val result = sut.deleteNotification(TEST_ADDRESS)

        assertFalse(result)
        coVerify(exactly = 0) { deleteInboxJointInvitationNotification(any(), any()) }
    }

    @Test
    fun `EXPECT false WHEN device id is not a valid number for deleteNotification`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "invalid"

        val result = sut.deleteNotification(TEST_ADDRESS)

        assertFalse(result)
        coVerify(exactly = 0) { deleteInboxJointInvitationNotification(any(), any()) }
    }

    private companion object {
        const val TEST_DEVICE_ID = "12345"
        const val TEST_DEVICE_ID_LONG = 12345L
        const val TEST_ADDRESS = "TEST_ADDRESS_123"
    }
}
