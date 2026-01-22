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

package com.algorand.wallet.jointaccount.transaction.data.mapper

import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponse
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponseItem
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

internal class JointSignRequestMapperTest {

    private lateinit var jointAccountDTOMapper: JointAccountDTOMapper
    private lateinit var mapper: JointSignRequestMapper

    @Before
    fun setup() {
        jointAccountDTOMapper = mockk()
        mapper = JointSignRequestMapper(jointAccountDTOMapper)
    }

    @Test
    fun `EXPECT null WHEN response is null`() {
        val result = mapper.mapToJointSignRequest(null)

        assertNull(result)
    }

    @Test
    fun `EXPECT id to be mapped correctly`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(id = "123")

        val result = mapper.mapToJointSignRequest(response)

        assertEquals("123", result?.id)
    }

    @Test
    fun `EXPECT joint account to be mapped correctly`() {
        val mockJointAccount = JointAccount(
            creationDatetime = "2024-01-01T00:00:00Z",
            address = "JOINT_ADDRESS",
            version = 1,
            threshold = 2,
            participantAddresses = listOf("ADDR1", "ADDR2")
        )
        val jointAccountResponse = JointAccountResponse(
            creationDatetime = "2024-01-01T00:00:00Z",
            address = "JOINT_ADDRESS",
            version = 1,
            threshold = 2,
            participantAddresses = listOf("ADDR1", "ADDR2")
        )
        every { jointAccountDTOMapper.mapToJointAccountDTO(jointAccountResponse) } returns mockJointAccount

        val response = createTestResponse(jointAccount = jointAccountResponse)

        val result = mapper.mapToJointSignRequest(response)

        assertNotNull(result?.jointAccount)
        assertEquals("JOINT_ADDRESS", result?.jointAccount?.address)
        assertEquals(2, result?.jointAccount?.threshold)
    }

    @Test
    fun `EXPECT proposer address and type to be mapped correctly`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(proposerAddress = "PROPOSER_ADDRESS", type = "payment")

        val result = mapper.mapToJointSignRequest(response)

        assertEquals("PROPOSER_ADDRESS", result?.proposerAddress)
        assertEquals("payment", result?.type)
    }

    @Test
    fun `EXPECT raw transaction lists to be mapped correctly`() {
        setupMockJointAccountMapper(null)
        val rawTransactionLists = listOf(listOf("raw_tx_1", "raw_tx_2"))
        val response = createTestResponse(rawTransactionLists = rawTransactionLists)

        val result = mapper.mapToJointSignRequest(response)

        assertEquals(rawTransactionLists, result?.rawTransactionLists)
    }

    @Test
    fun `EXPECT transaction lists to be mapped correctly`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(
            transactionLists = listOf(
                SignRequestTransactionListResponse(
                    id = "txn_1",
                    rawTransactions = listOf("raw_tx_1"),
                    firstValidBlock = "100",
                    lastValidBlock = "200",
                    responses = listOf(
                        SignRequestTransactionListResponseItem(
                            address = "ADDR1",
                            response = "signed",
                            signatures = listOf("sig_1")
                        )
                    ),
                    expectedExpireDatetime = "2024-01-02T00:00:00Z"
                )
            )
        )

        val result = mapper.mapToJointSignRequest(response)

        assertNotNull(result?.transactionLists)
        assertEquals(1, result?.transactionLists?.size)
        assertEquals("txn_1", result?.transactionLists?.first()?.id)
        assertEquals(listOf("raw_tx_1"), result?.transactionLists?.first()?.rawTransactions)
    }

    @Test
    fun `EXPECT PENDING status WHEN status is pending`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(status = "pending")

        val result = mapper.mapToJointSignRequest(response)

        assertEquals(SignRequestStatus.PENDING, result?.status)
    }

    @Test
    fun `EXPECT READY status WHEN status is ready`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(status = "ready")

        val result = mapper.mapToJointSignRequest(response)

        assertEquals(SignRequestStatus.READY, result?.status)
    }

    @Test
    fun `EXPECT CONFIRMED status WHEN status is confirmed`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(status = "confirmed")

        val result = mapper.mapToJointSignRequest(response)

        assertEquals(SignRequestStatus.CONFIRMED, result?.status)
    }

    @Test
    fun `EXPECT null status WHEN status is null`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(status = null)

        val result = mapper.mapToJointSignRequest(response)

        assertNull(result?.status)
    }

    @Test
    fun `EXPECT SIGNED response type WHEN response is signed`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(
            transactionLists = listOf(
                SignRequestTransactionListResponse(
                    id = "txn_1",
                    rawTransactions = null,
                    firstValidBlock = null,
                    lastValidBlock = null,
                    responses = listOf(
                        SignRequestTransactionListResponseItem(
                            address = "ADDR1",
                            response = "signed",
                            signatures = listOf("sig_1")
                        )
                    ),
                    expectedExpireDatetime = null
                )
            )
        )

        val result = mapper.mapToJointSignRequest(response)

        val responseItem = result?.transactionLists?.first()?.responses?.first()
        assertEquals(SignRequestResponseType.SIGNED, responseItem?.response)
    }

    @Test
    fun `EXPECT DECLINED response type WHEN response is declined`() {
        setupMockJointAccountMapper(null)
        val response = createTestResponse(
            transactionLists = listOf(
                SignRequestTransactionListResponse(
                    id = "txn_1",
                    rawTransactions = null,
                    firstValidBlock = null,
                    lastValidBlock = null,
                    responses = listOf(
                        SignRequestTransactionListResponseItem(
                            address = "ADDR1",
                            response = "declined",
                            signatures = null
                        )
                    ),
                    expectedExpireDatetime = null
                )
            )
        )

        val result = mapper.mapToJointSignRequest(response)

        val responseItem = result?.transactionLists?.first()?.responses?.first()
        assertEquals(SignRequestResponseType.DECLINED, responseItem?.response)
    }

    private fun setupMockJointAccountMapper(returnValue: JointAccount?) {
        every { jointAccountDTOMapper.mapToJointAccountDTO(any()) } returns returnValue
    }

    private fun createTestResponse(
        id: String? = "123",
        jointAccount: JointAccountResponse? = null,
        proposerAddress: String? = "PROPOSER",
        type: String? = "payment",
        rawTransactionLists: List<List<String>>? = null,
        transactionLists: List<SignRequestTransactionListResponse>? = null,
        expectedExpireDatetime: String? = null,
        status: String? = "pending"
    ) = JointSignRequestResponse(
        id = id,
        jointAccount = jointAccount,
        proposerAddress = proposerAddress,
        type = type,
        rawTransactionLists = rawTransactionLists,
        transactionLists = transactionLists,
        expectedExpireDatetime = expectedExpireDatetime,
        status = status
    )
}
