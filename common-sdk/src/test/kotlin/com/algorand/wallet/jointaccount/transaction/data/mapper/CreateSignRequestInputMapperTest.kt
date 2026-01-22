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

import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
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
        assertEquals(expected.transactionSignatureLists, result.transactionSignatureLists)
    }

    @Test
    fun `EXPECT empty lists WHEN lists are empty`() {
        val input = createTestInput().copy(
            rawTransactionLists = emptyList(),
            transactionSignatureLists = emptyList()
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(emptyList<List<String>>(), result.rawTransactionLists)
        assertEquals(emptyList<List<String?>>(), result.transactionSignatureLists)
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
            transactionSignatureLists = multipleSignatureLists
        )

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(multipleRawTxLists, result.rawTransactionLists)
        assertEquals(multipleSignatureLists, result.transactionSignatureLists)
    }

    @Test
    fun `EXPECT all null signatures WHEN all signatures are null`() {
        val nullSignatures = listOf(listOf<String?>(null, null, null))
        val input = createTestInput().copy(transactionSignatureLists = nullSignatures)

        val result = mapper.mapToProposeJointSignRequestRequest(input)

        assertEquals(nullSignatures, result.transactionSignatureLists)
    }

    private fun createTestInput() = CreateSignRequestInput(
        jointAccountAddress = TEST_JOINT_ACCOUNT_ADDRESS,
        proposerAddress = TEST_PROPOSER_ADDRESS,
        type = TEST_TYPE,
        rawTransactionLists = TEST_RAW_TRANSACTION_LISTS,
        transactionSignatureLists = TEST_SIGNATURE_LISTS
    )

    private fun createExpectedRequest() = com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestRequest(
        jointAccountAddress = TEST_JOINT_ACCOUNT_ADDRESS,
        proposerAddress = TEST_PROPOSER_ADDRESS,
        type = TEST_TYPE,
        rawTransactionLists = TEST_RAW_TRANSACTION_LISTS,
        transactionSignatureLists = TEST_SIGNATURE_LISTS
    )

    private companion object {
        const val TEST_JOINT_ACCOUNT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PROPOSER_ADDRESS = "PROPOSER_ADDRESS"
        const val TEST_TYPE = "payment"
        val TEST_RAW_TRANSACTION_LISTS = listOf(listOf("raw_tx_1", "raw_tx_2"))
        val TEST_SIGNATURE_LISTS = listOf(listOf("sig_1", null))
    }
}
