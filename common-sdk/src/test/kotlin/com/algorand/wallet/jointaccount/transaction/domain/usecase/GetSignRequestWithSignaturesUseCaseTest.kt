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
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestTransactionList
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestTransactionListItem
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GetSignRequestWithSignaturesUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = GetSignRequestWithSignaturesUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository returns sign request`() = runTest {
        coEvery { repository.searchSignRequests(any()) } returns PeraResult.Success(listOf(createSignRequestDTO()))

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val data = (result as PeraResult.Success).data
        assertEquals(123L, data.id)
        assertEquals("payment", data.type)
        assertEquals("PROPOSER_ADDRESS", data.proposerAddress)
    }

    @Test
    fun `EXPECT error WHEN repository returns empty list`() = runTest {
        coEvery { repository.searchSignRequests(any()) } returns PeraResult.Success(emptyList())

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
        assertEquals("Sign request not found", (result as PeraResult.Error).exception.message)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.searchSignRequests(any()) } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
        assertEquals(exception, (result as PeraResult.Error).exception)
    }

    @Test
    fun `EXPECT transaction lists mapped correctly`() = runTest {
        coEvery { repository.searchSignRequests(any()) } returns
            PeraResult.Success(listOf(createSignRequestDTOWithTransactionLists()))

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val data = (result as PeraResult.Success).data
        assertNotNull(data.transactionLists)
        assertEquals(1, data.transactionLists?.size)
        assertEquals(listOf("raw_tx_1"), data.transactionLists?.first()?.rawTransactions)
        assertEquals(100L, data.transactionLists?.first()?.firstValidBlock)
        assertEquals(200L, data.transactionLists?.first()?.lastValidBlock)
    }

    @Test
    fun `EXPECT participant signatures mapped correctly`() = runTest {
        coEvery { repository.searchSignRequests(any()) } returns
            PeraResult.Success(listOf(createSignRequestDTOWithTransactionLists()))

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val responses = (result as PeraResult.Success).data.transactionLists?.first()?.responses
        assertNotNull(responses)
        assertEquals(1, responses?.size)
        assertEquals("PARTICIPANT_ADDRESS", responses?.first()?.address)
        assertEquals(SignRequestResponseType.SIGNED, responses?.first()?.type)
    }

    @Test
    fun `EXPECT matching sign request WHEN repository returns multiple`() = runTest {
        val matching = createSignRequestDTO()
        val other = createSignRequestDTO().copy(id = "456")
        coEvery { repository.searchSignRequests(any()) } returns PeraResult.Success(listOf(matching, other))

        val result = sut(TEST_DEVICE_ID, TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        assertEquals(123L, (result as PeraResult.Success).data.id)
    }

    private fun createSignRequestDTO() = JointSignRequest(
        id = "123",
        jointAccount = JointAccount(
            creationDatetime = "2024-01-01T00:00:00Z",
            address = "JOINT_ADDRESS",
            version = 1,
            threshold = 2,
            participantAddresses = listOf("ADDR1", "ADDR2", "ADDR3")
        ),
        proposerAddress = "PROPOSER_ADDRESS",
        type = "payment",
        rawTransactionLists = listOf(listOf("raw_tx_1")),
        transactionLists = null,
        expectedExpireDatetime = "2024-01-02T00:00:00Z",
        status = SignRequestStatus.PENDING
    )

    private fun createSignRequestDTOWithTransactionLists() = createSignRequestDTO().copy(
        transactionLists = listOf(
            JointSignRequestTransactionList(
                id = "txn_list_1",
                rawTransactions = listOf("raw_tx_1"),
                firstValidBlock = "100",
                lastValidBlock = "200",
                responses = listOf(
                    JointSignRequestTransactionListItem(
                        address = "PARTICIPANT_ADDRESS",
                        response = SignRequestResponseType.SIGNED,
                        signatures = listOf("sig_1", "sig_2")
                    )
                ),
                expectedExpireDatetime = "2024-01-02T00:00:00Z"
            )
        )
    )

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_SIGN_REQUEST_ID = "123"
    }
}
