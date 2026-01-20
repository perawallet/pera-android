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

import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ProposeJointSignRequestDTOMapperTest {

    private val mapper = ProposeJointSignRequestDTOMapper()

    private val testJointAccountAddress = "JOINT_ADDRESS_123"
    private val testProposerAddress = "PROPOSER_ADDRESS"
    private val testType = "payment"
    private val testRawTransactionLists = listOf(listOf("raw_tx_1", "raw_tx_2"))
    private val testSignatureLists = listOf(listOf("sig_1", null))

    @Test
    fun `EXPECT joint account address to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(testJointAccountAddress, result.jointAccountAddress)
    }

    @Test
    fun `EXPECT proposer address to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(testProposerAddress, result.proposerAddress)
    }

    @Test
    fun `EXPECT type to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(testType, result.type)
    }

    @Test
    fun `EXPECT raw transaction lists to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(testRawTransactionLists, result.rawTransactionLists)
    }

    @Test
    fun `EXPECT transaction signature lists to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(testSignatureLists, result.transactionSignatureLists)
    }

    @Test
    fun `EXPECT empty lists WHEN lists are empty`() {
        val dto = ProposeJointSignRequestDTO(
            jointAccountAddress = testJointAccountAddress,
            proposerAddress = testProposerAddress,
            type = testType,
            rawTransactionLists = emptyList(),
            transactionSignatureLists = emptyList()
        )

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

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
        val dto = ProposeJointSignRequestDTO(
            jointAccountAddress = testJointAccountAddress,
            proposerAddress = testProposerAddress,
            type = "asset_transfer",
            rawTransactionLists = multipleRawTxLists,
            transactionSignatureLists = multipleSignatureLists
        )

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(multipleRawTxLists, result.rawTransactionLists)
        assertEquals(multipleSignatureLists, result.transactionSignatureLists)
    }

    @Test
    fun `EXPECT all null signatures WHEN all signatures are null`() {
        val nullSignatures = listOf(listOf<String?>(null, null, null))
        val dto = ProposeJointSignRequestDTO(
            jointAccountAddress = testJointAccountAddress,
            proposerAddress = testProposerAddress,
            type = testType,
            rawTransactionLists = testRawTransactionLists,
            transactionSignatureLists = nullSignatures
        )

        val result = mapper.mapToProposeJointSignRequestRequest(dto)

        assertEquals(nullSignatures, result.transactionSignatureLists)
    }

    private fun createTestDTO(): ProposeJointSignRequestDTO {
        return ProposeJointSignRequestDTO(
            jointAccountAddress = testJointAccountAddress,
            proposerAddress = testProposerAddress,
            type = testType,
            rawTransactionLists = testRawTransactionLists,
            transactionSignatureLists = testSignatureLists
        )
    }
}
