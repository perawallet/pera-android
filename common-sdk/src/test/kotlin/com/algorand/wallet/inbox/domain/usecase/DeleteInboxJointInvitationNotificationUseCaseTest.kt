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

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DeleteInboxJointInvitationNotificationUseCaseTest {

    private val repository: InboxApiRepository = mockk()
    private val sut = DeleteInboxJointInvitationNotificationUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        coEvery { repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS) } returns PeraResult.Success(Unit)

        val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        assertTrue(result is PeraResult.Success)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS) } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT correct parameters passed to repository`() = runTest {
        coEvery { repository.deleteJointInvitationNotification(any(), any()) } returns PeraResult.Success(Unit)

        sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        coVerify { repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS) }
    }

    @Test
    fun `EXPECT repository called with different device id`() = runTest {
        val differentDeviceId = 99999L
        coEvery { repository.deleteJointInvitationNotification(differentDeviceId, TEST_JOINT_ADDRESS) } returns PeraResult.Success(Unit)

        val result = sut(differentDeviceId, TEST_JOINT_ADDRESS)

        assertTrue(result is PeraResult.Success)
        coVerify { repository.deleteJointInvitationNotification(differentDeviceId, TEST_JOINT_ADDRESS) }
    }

    @Test
    fun `EXPECT repository called with different joint address`() = runTest {
        val differentAddress = "DIFFERENT_JOINT_ADDRESS"
        coEvery { repository.deleteJointInvitationNotification(TEST_DEVICE_ID, differentAddress) } returns PeraResult.Success(Unit)

        val result = sut(TEST_DEVICE_ID, differentAddress)

        assertTrue(result is PeraResult.Success)
        coVerify { repository.deleteJointInvitationNotification(TEST_DEVICE_ID, differentAddress) }
    }

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
    }
}
