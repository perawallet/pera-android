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

package com.algorand.android.modules.addaccount.joint.creation.domain.usecase

import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.joint.core.domain.repository.JointAccountRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DeleteInboxJointInvitationNotificationUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = DeleteInboxJointInvitationNotificationUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        coEvery { repository.deleteInboxJointInvitationNotification(any(), any()) } returns Result.Success(Unit)

        val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        coEvery { repository.deleteInboxJointInvitationNotification(any(), any()) } returns Result.Error(Exception())

        val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        assertTrue(result is Result.Error)
    }

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
    }
}
