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

package com.algorand.wallet.swap.domain.validation.rules

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.AlgorandAddress
import com.algorand.wallet.swap.domain.usecase.GetParsedSwapTransactions
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwapRekeyedTransactionValidationRuleTest {

    private val getParsedSwapTransactions: GetParsedSwapTransactions = mockk()

    private val sut = SwapRekeyedTransactionValidationRule(getParsedSwapTransactions)

    @Test
    fun `EXPECT false WHEN there is transaction that rekeys local address`() {
        val rekeyTxn = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(LOCAL_ADDRESS_1, null),
            rekeyAddress = AlgorandAddress("some address", null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(rekeyTxn)

        val result = sut(VALIDATION_DATA)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN there is no transaction that rekeys local address`() {
        val rekeyTxn1 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress("not local address", null),
            rekeyAddress = AlgorandAddress("random address", null)
        )
        val rekeyTxn2 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress("not local address", null),
            rekeyAddress = AlgorandAddress(null, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(rekeyTxn1, rekeyTxn2)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN there is no rekey transaction`() {
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(VALID_TRANSACTION)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    private companion object {
        const val LOCAL_ADDRESS_1 = "LOCAL_ADDRESS_1"
        const val LOCAL_ADDRESS_2 = "LOCAL_ADDRESS_2"
        val VALID_TRANSACTION = SwapValidationRuleTransactionBuilder.buildEmptyTransaction()
        val SIGNED_TXNS: List<ByteArray?> = peraFixture()
        val UNSIGNED_TXNS: List<ByteArray?> = peraFixture()
        val VALIDATION_DATA = SwapTransactionValidationData(
            quote = peraFixture(),
            signedTransactions = SIGNED_TXNS,
            unsignedTransactions = UNSIGNED_TXNS,
            localAddresses = listOf(LOCAL_ADDRESS_1, LOCAL_ADDRESS_2)
        )
    }
}
