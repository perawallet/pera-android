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

package com.algorand.wallet.jointaccount.transaction.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ProposeJointSignRequestUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = ProposeJointSignRequestUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val expectedResult = mockk<JointSignRequest>()
        coEvery { repository.proposeSignRequest(any()) } returns PeraResult.Success(expectedResult)

        val result = sut(TEST_JOINT_ADDRESS, TEST_PROPOSER, TEST_TYPE, TEST_RAW_TX, TEST_SIGS)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedResult, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.proposeSignRequest(any()) } returns PeraResult.Error(exception)

        val result = sut(TEST_JOINT_ADDRESS, TEST_PROPOSER, TEST_TYPE, TEST_RAW_TX, TEST_SIGS)

        assertTrue(result is PeraResult.Error)
    }

    private companion object {
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PROPOSER = "PROPOSER_ADDRESS"
        const val TEST_TYPE = "payment"
        val TEST_RAW_TX = listOf(listOf("raw_tx_1", "raw_tx_2"))
        val TEST_SIGS = listOf(listOf("sig_1", null))
    }
}
