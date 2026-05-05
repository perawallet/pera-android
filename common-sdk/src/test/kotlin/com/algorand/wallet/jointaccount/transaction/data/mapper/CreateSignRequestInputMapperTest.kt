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

import com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResponseInput
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestType
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CreateSignRequestInputMapperTest {

    private val mapper = CreateSignRequestInputMapper()

    @Test
    fun `EXPECT all fields to be mapped correctly`() {
        val input = createTestInput()
        val expected = createExpectedRequest()

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(expected.jointAccountAddress, result.jointAccountAddress)
        assertEquals(expected.proposerAddress, result.proposerAddress)
        assertEquals(expected.type, result.type)
        assertEquals(expected.rawTransactionLists, result.rawTransactionLists)
        assertEquals(expected.responses.size, result.responses.size)
        assertEquals(expected.responses[0].address, result.responses[0].address)
        assertEquals(expected.responses[0].response, result.responses[0].response)
        assertEquals(expected.responses[0].signatures, result.responses[0].signatures)
        assertEquals(expected.responses[0].deviceId, result.responses[0].deviceId)
    }

    @Test
    fun `EXPECT empty lists WHEN lists are empty`() {
        val input = createTestInput().copy(
            rawTransactionLists = emptyList(),
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = TEST_PROPOSER_ADDRESS,
                    responseType = ProposeJointSignRequestResult.SIGNED,
                    signatures = emptyList()
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(emptyList<List<String>>(), result.rawTransactionLists)
        assertEquals(1, result.responses.size)
        assertEquals(emptyList<List<String?>>(), result.responses[0].signatures)
    }

    @Test
    fun `EXPECT multiple transaction lists to be mapped correctly`() {
        val multipleRawTxLists = listOf(
            listOf("tx_1_a", "tx_1_b"),
            listOf("tx_2_a", "tx_2_b", "tx_2_c")
        )
        val multipleSignatureLists = listOf(
            listOf("sig_1_a", "sig_1_b"),
            listOf("sig_2_a", null, "sig_2_c")
        )
        val input = createTestInput().copy(
            rawTransactionLists = multipleRawTxLists,
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = TEST_PROPOSER_ADDRESS,
                    responseType = ProposeJointSignRequestResult.SIGNED,
                    signatures = multipleSignatureLists
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(multipleRawTxLists, result.rawTransactionLists)
        assertEquals(1, result.responses.size)
        assertEquals(multipleSignatureLists, result.responses[0].signatures)
    }

    @Test
    fun `EXPECT all null signatures WHEN all signatures are null`() {
        val nullSignatures = listOf(listOf<String?>(null, null, null))
        val input = createTestInput().copy(
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = TEST_PROPOSER_ADDRESS,
                    responseType = ProposeJointSignRequestResult.SIGNED,
                    signatures = nullSignatures
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(1, result.responses.size)
        assertEquals(nullSignatures, result.responses[0].signatures)
    }

    @Test
    fun `EXPECT device id to be mapped WHEN provided`() {
        val deviceId = "test-device-id"
        val input = createTestInput().copy(
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = TEST_PROPOSER_ADDRESS,
                    responseType = ProposeJointSignRequestResult.SIGNED,
                    signatures = TEST_SIGNATURE_LISTS,
                    deviceId = deviceId
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(1, result.responses.size)
        assertEquals(deviceId, result.responses[0].deviceId)
    }

    @Test
    fun `EXPECT declined response type WHEN declined enum is used`() {
        val input = createTestInput().copy(
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = TEST_PROPOSER_ADDRESS,
                    responseType = ProposeJointSignRequestResult.DECLINED,
                    signatures = TEST_SIGNATURE_LISTS
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(1, result.responses.size)
        assertEquals("declined", result.responses[0].response)
    }

    @Test
    fun `EXPECT multiple responses WHEN multiple responses are provided`() {
        val address1 = "ADDRESS_1"
        val address2 = "ADDRESS_2"
        val signatures1 = listOf(listOf("sig_1", "sig_2"))
        val signatures2 = listOf(listOf("sig_3", null))
        val input = createTestInput().copy(
            responses = listOf(
                ProposeJointSignRequestResponseInput(
                    address = address1,
                    responseType = ProposeJointSignRequestResult.SIGNED,
                    signatures = signatures1
                ),
                ProposeJointSignRequestResponseInput(
                    address = address2,
                    responseType = ProposeJointSignRequestResult.DECLINED,
                    signatures = signatures2,
                    deviceId = "device-123"
                )
            )
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(2, result.responses.size)
        assertEquals(address1, result.responses[0].address)
        assertEquals("signed", result.responses[0].response)
        assertEquals(signatures1, result.responses[0].signatures)
        assertEquals(null, result.responses[0].deviceId)
        assertEquals(address2, result.responses[1].address)
        assertEquals("declined", result.responses[1].response)
        assertEquals(signatures2, result.responses[1].signatures)
        assertEquals("device-123", result.responses[1].deviceId)
    }

    private fun createTestInput() = CreateSignRequestInput(
        jointAccountAddress = TEST_JOINT_ACCOUNT_ADDRESS,
        proposerAddress = TEST_PROPOSER_ADDRESS,
        type = TEST_SIGN_REQUEST_TYPE,
        rawTransactionLists = TEST_RAW_TRANSACTION_LISTS,
        responses = listOf(
            ProposeJointSignRequestResponseInput(
                address = TEST_PROPOSER_ADDRESS,
                responseType = TEST_RESPONSE_TYPE,
                signatures = TEST_SIGNATURE_LISTS
            )
        )
    )

    private fun createExpectedRequest() = com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestRequest(
        jointAccountAddress = TEST_JOINT_ACCOUNT_ADDRESS,
        proposerAddress = TEST_PROPOSER_ADDRESS,
        type = TEST_SIGN_REQUEST_TYPE.value,
        rawTransactionLists = TEST_RAW_TRANSACTION_LISTS,
        responses = listOf(
            ProposeJointSignRequestResponse(
                address = TEST_PROPOSER_ADDRESS,
                response = TEST_RESPONSE_TYPE.value,
                signatures = TEST_SIGNATURE_LISTS,
                deviceId = null
            )
        )
    )

    private companion object {
        const val TEST_JOINT_ACCOUNT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PROPOSER_ADDRESS = "PROPOSER_ADDRESS"
        val TEST_SIGN_REQUEST_TYPE = SignRequestType.ASYNC
        val TEST_RESPONSE_TYPE = ProposeJointSignRequestResult.SIGNED
        val TEST_RAW_TRANSACTION_LISTS = listOf(listOf("raw_tx_1", "raw_tx_2"))
        val TEST_SIGNATURE_LISTS = listOf(listOf("sig_1", null))
    }
}
