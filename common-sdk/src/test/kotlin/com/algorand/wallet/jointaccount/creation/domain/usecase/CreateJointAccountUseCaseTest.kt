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

package com.algorand.wallet.jointaccount.creation.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountInput
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateJointAccountUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = CreateJointAccountUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val expectedResult = createJointAccount()
        coEvery { repository.createJointAccount(any()) } returns PeraResult.Success(expectedResult)

        val result = sut(TEST_ADDRESSES, TEST_THRESHOLD, TEST_VERSION)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedResult, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.createJointAccount(any()) } returns PeraResult.Error(exception)

        val result = sut(TEST_ADDRESSES, TEST_THRESHOLD, TEST_VERSION)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT correct input passed to repository`() = runTest {
        val inputSlot = slot<CreateJointAccountInput>()
        coEvery { repository.createJointAccount(capture(inputSlot)) } returns PeraResult.Success(createJointAccount())

        sut(TEST_ADDRESSES, TEST_THRESHOLD, TEST_VERSION)

        coVerify { repository.createJointAccount(any()) }
        assertEquals(TEST_ADDRESSES, inputSlot.captured.participantAddresses)
        assertEquals(TEST_THRESHOLD, inputSlot.captured.threshold)
        assertEquals(TEST_VERSION, inputSlot.captured.version)
    }

    @Test
    fun `EXPECT repository called with single participant`() = runTest {
        val singleParticipant = listOf("ADDR1")
        coEvery { repository.createJointAccount(any()) } returns PeraResult.Success(createJointAccount())

        sut(singleParticipant, 1, TEST_VERSION)

        coVerify { repository.createJointAccount(any()) }
    }

    @Test
    fun `EXPECT repository called with threshold equal to participant count`() = runTest {
        coEvery { repository.createJointAccount(any()) } returns PeraResult.Success(createJointAccount())

        sut(TEST_ADDRESSES, TEST_ADDRESSES.size, TEST_VERSION)

        coVerify { repository.createJointAccount(any()) }
    }

    @Test
    fun `EXPECT repository called with threshold of 1`() = runTest {
        coEvery { repository.createJointAccount(any()) } returns PeraResult.Success(createJointAccount())

        sut(TEST_ADDRESSES, 1, TEST_VERSION)

        coVerify { repository.createJointAccount(any()) }
    }

    private fun createJointAccount() = JointAccount(
        creationDatetime = "2025-01-01T00:00:00Z",
        address = "JOINT_ADDRESS_123",
        version = TEST_VERSION,
        threshold = TEST_THRESHOLD,
        participantAddresses = TEST_ADDRESSES
    )

    private companion object {
        val TEST_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
    }
}
