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
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class AddJointAccountSignatureUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = AddJointAccountSignatureUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val expectedResult = mockk<JointSignRequestDTO>()
        coEvery { repository.addSignature(any(), any()) } returns PeraResult.Success(expectedResult)

        val result = sut(TEST_SIGN_REQUEST_ID, createResponseDTO())

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedResult, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.addSignature(any(), any()) } returns PeraResult.Error(exception)

        val result = sut(TEST_SIGN_REQUEST_ID, createResponseDTO())

        assertTrue(result is PeraResult.Error)
        assertEquals(exception, (result as PeraResult.Error).exception)
    }

    private fun createResponseDTO() = SignRequestTransactionListResponseDTO(
        address = TEST_ADDRESS,
        response = SignRequestResponseType.SIGNED,
        signatures = listOf(listOf("signature_1", "signature_2")),
        deviceId = "device_123"
    )

    private companion object {
        const val TEST_SIGN_REQUEST_ID = "sign_request_123"
        const val TEST_ADDRESS = "PARTICIPANT_ADDRESS"
    }
}
