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

package com.algorand.wallet.jointaccount.transaction.domain

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

internal class MultisigTransactionAssemblerTest {

    private val assembler = MultisigTransactionAssembler()

    @Test
    fun `EXPECT success with empty list WHEN rawTransactionsBase64 is empty`() {
        val result = assembler.assemble(
            rawTransactionsBase64 = emptyList(),
            participantAddresses = listOf(TEST_ADDRESS_1, TEST_ADDRESS_2),
            version = 1,
            threshold = 2,
            responses = emptyList()
        )
        assertTrue(result is PeraResult.Success)
        assertEquals(emptyList<ByteArray>(), (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN threshold is not met`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64),
            participantAddresses = listOf(TEST_ADDRESS_1, TEST_ADDRESS_2),
            version = 1,
            threshold = 2,
            responses = responses
        )
        assertTrue(result is PeraResult.Error)
        assertTrue((result as PeraResult.Error).exception.message?.contains("Not enough valid signatures") == true)
    }

    @Test
    fun `EXPECT error WHEN invalid base64 raw transaction`() {
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf("!!!invalid-base64!!!"),
            participantAddresses = listOf(TEST_ADDRESS_1),
            version = 1,
            threshold = 1,
            responses = responses
        )
        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN participant address is invalid`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = "INVALID_ADDRESS",
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64),
            participantAddresses = listOf("INVALID_ADDRESS"),
            version = 1,
            threshold = 1,
            responses = responses
        )
        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT success WHEN threshold is met with valid signatures`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            ),
            ParticipantSignature(
                address = TEST_ADDRESS_2,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64),
            participantAddresses = listOf(TEST_ADDRESS_1, TEST_ADDRESS_2),
            version = 1,
            threshold = 2,
            responses = responses
        )
        assertTrue(result is PeraResult.Success)
        val signedList = (result as PeraResult.Success).data
        assertEquals(1, signedList.size)
        assertTrue(signedList[0].isNotEmpty())
    }

    @Test
    fun `EXPECT only SIGNED responses used WHEN mix of SIGNED and DECLINED`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            ),
            ParticipantSignature(
                address = TEST_ADDRESS_2,
                signatures = listOf(null),
                type = SignRequestResponseType.DECLINED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64),
            participantAddresses = listOf(TEST_ADDRESS_1, TEST_ADDRESS_2),
            version = 1,
            threshold = 1,
            responses = responses
        )
        assertTrue(result is PeraResult.Success)
    }

    @Test
    fun `EXPECT multiple signed transactions WHEN multiple raw transactions provided`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64, sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64, rawTxBase64),
            participantAddresses = listOf(TEST_ADDRESS_1),
            version = 1,
            threshold = 1,
            responses = responses
        )
        assertTrue(result is PeraResult.Success)
        assertEquals(2, (result as PeraResult.Success).data.size)
    }

    @Test
    fun `EXPECT error WHEN signature list is shorter than transaction list`() {
        val rawTxBase64 = Base64.getEncoder().encodeToString(DUMMY_TX_BYTES)
        val sigBase64 = Base64.getEncoder().encodeToString(DUMMY_SIGNATURE)
        val responses = listOf(
            ParticipantSignature(
                address = TEST_ADDRESS_1,
                signatures = listOf(sigBase64),
                type = SignRequestResponseType.SIGNED
            )
        )
        val result = assembler.assemble(
            rawTransactionsBase64 = listOf(rawTxBase64, rawTxBase64),
            participantAddresses = listOf(TEST_ADDRESS_1),
            version = 1,
            threshold = 1,
            responses = responses
        )
        assertTrue(result is PeraResult.Error)
    }

    private companion object {
        const val TEST_ADDRESS_1 = "AEBAGBAFAYDQQCIKBMGA2DQPCAIREEYUCULBOGAZDINRYHI6D4QDTYK3BA"
        const val TEST_ADDRESS_2 = "EERCGJBFEYTSQKJKFMWC2LRPGAYTEMZUGU3DOOBZHI5TYPJ6H5APQGQK7A"
        val DUMMY_TX_BYTES = ByteArray(32) { (it + 1).toByte() }
        val DUMMY_SIGNATURE = ByteArray(64) { (it + 100).toByte() }
    }
}
