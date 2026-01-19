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
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateJointAccountUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = CreateJointAccountUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val expectedResult = mockk<JointAccountDTO>()
        coEvery { repository.createJointAccount(any()) } returns Result.Success(expectedResult)

        val result = sut(TEST_ADDRESSES, TEST_THRESHOLD, TEST_VERSION)

        assertTrue(result is Result.Success)
        assertEquals(expectedResult, (result as Result.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.createJointAccount(any()) } returns Result.Error(exception)

        val result = sut(TEST_ADDRESSES, TEST_THRESHOLD, TEST_VERSION)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT InsufficientParticipants error WHEN participant count is less than minimum`() = runTest {
        val result = sut(listOf("ADDR1"), TEST_THRESHOLD, TEST_VERSION)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is JointAccountValidationException.InsufficientParticipants)
    }

    @Test
    fun `EXPECT InvalidThreshold error WHEN threshold is zero`() = runTest {
        val result = sut(TEST_ADDRESSES, 0, TEST_VERSION)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is JointAccountValidationException.InvalidThreshold)
    }

    @Test
    fun `EXPECT InvalidThreshold error WHEN threshold exceeds participant count`() = runTest {
        val result = sut(TEST_ADDRESSES, 5, TEST_VERSION)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is JointAccountValidationException.InvalidThreshold)
    }

    @Test
    fun `EXPECT success WHEN threshold equals participant count`() = runTest {
        coEvery { repository.createJointAccount(any()) } returns Result.Success(mockk())

        val result = sut(TEST_ADDRESSES, 3, TEST_VERSION)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `EXPECT success WHEN threshold is 1`() = runTest {
        coEvery { repository.createJointAccount(any()) } returns Result.Success(mockk())

        val result = sut(TEST_ADDRESSES, 1, TEST_VERSION)

        assertTrue(result is Result.Success)
    }

    private companion object {
        val TEST_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
    }
}
