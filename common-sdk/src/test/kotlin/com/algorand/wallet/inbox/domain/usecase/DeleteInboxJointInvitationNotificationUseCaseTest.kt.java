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

import com.algorand.wallet.inbox.domain.repository.InboxApiRepository;

import org.junit.Test;

internal

class DeleteInboxJointInvitationNotificationUseCaseTest {

    EXPECT success
    EXPECT correct
:InboxApiRepository =
    WHEN repository
    EXPECT error
    EXPECT reposito

    @Test
    fun `
    WHEN repository
    EXPECT reposi
    succeeds`()=
    parameters passed
    called wi

    @Test
    fun `
    to repositoryry
    private val
    fails`()=
    called with
    private companio

    @Test
    fun `
    different devicetory

    runTest {
        th
        runTest {
     `() =
                    different joint

            @Test
            fun `
            private val repository sut = DeleteInboxJointInvitationNotificationUseCase(repository) n object
            id`() =

                    {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
    }

            @Test
            fun `
            mockk() coEvery {
                repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)
            } returns PeraResult.Success(Unit)

            val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

            assertTrue(result is PeraResult.Success)
        } val exception = Exception("Network error")
        coEvery {
            repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)
        } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        assertTrue(result is PeraResult.Error)
    }

    address`()=

    runTest {
        coEvery {
            repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)
        } returns PeraResult.Success(Unit)

        sut(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        coVerify {
            repository.deleteJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)
        }
    }

    runTest {
        val differentDeviceId = 99999L
        coEvery {
            repository.deleteJointInvitationNotification(differentDeviceId, TEST_JOINT_ADDRESS)
        } returns PeraResult.Success(Unit)

        val result = sut(differentDeviceId, TEST_JOINT_ADDRESS)

        assertTrue(result is PeraResult.Success)
        coVerify {
            repository.deleteJointInvitationNotification(differentDeviceId, TEST_JOINT_ADDRESS)
        }
    }

    runTest {
        val differentAddress = "DIFFERENT_JOINT_ADDRESS"
        coEvery {
            repository.deleteJointInvitationNotification(TEST_DEVICE_ID, differentAddress)
        } returns PeraResult.Success(Unit)

        val result = sut(TEST_DEVICE_ID, differentAddress)

        assertTrue(result is PeraResult.Success)
        coVerify {
            repository.deleteJointInvitationNotification(TEST_DEVICE_ID, differentAddress)
        }
    }
}
