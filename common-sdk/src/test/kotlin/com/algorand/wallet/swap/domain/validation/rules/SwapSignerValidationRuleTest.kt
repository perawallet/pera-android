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
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.usecase.GetParsedSwapTransactions
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwapSignerValidationRuleTest {

    private val getParsedSwapTransactions: GetParsedSwapTransactions = mockk()

    private val sut = SwapSignerValidationRule(getParsedSwapTransactions)

    @Test
    fun `EXPECT true WHEN transaction sender is quote account address`() {
        val transaction = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(QUOTE_ACCOUNT_ADDRESS, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN transaction sender is not a local address`() {
        val transaction = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress("EXTERNAL_ADDRESS", null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN transaction sender is local address`() {
        val transaction = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(LOCAL_ADDRESS_1, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(VALIDATION_DATA)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN multiple transactions with quote account address as sender`() {
        val transaction1 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(QUOTE_ACCOUNT_ADDRESS, null)
        )
        val transaction2 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(QUOTE_ACCOUNT_ADDRESS, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN multiple transactions with external addresses as sender`() {
        val transaction1 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress("EXTERNAL_ADDRESS_1", null)
        )
        val transaction2 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress("EXTERNAL_ADDRESS_2", null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN one transaction sender is local address`() {
        val transaction1 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(QUOTE_ACCOUNT_ADDRESS, null)
        )
        val transaction2 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(LOCAL_ADDRESS_1, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(VALIDATION_DATA)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN transaction sender is null`() {
        val transaction1 = VALID_TRANSACTION.copy(
            senderAddress = null
        )
        val transaction2 = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(null, null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(VALIDATION_DATA)

        assertTrue(result)
    }

    private companion object {
        const val QUOTE_ACCOUNT_ADDRESS = "QUOTE_ACCOUNT_ADDRESS"
        const val LOCAL_ADDRESS_1 = "LOCAL_ADDRESS_1"
        const val LOCAL_ADDRESS_2 = "LOCAL_ADDRESS_2"
        val VALID_TRANSACTION = SwapValidationRuleTransactionBuilder.buildEmptyTransaction()
        val SIGNED_TXNS: List<ByteArray?> = peraFixture()
        val UNSIGNED_TXNS: List<ByteArray?> = peraFixture()
        val QUOTE = peraFixture<SwapQuoteV2>().copy(accountAddress = QUOTE_ACCOUNT_ADDRESS)
        val VALIDATION_DATA = SwapTransactionValidationData(
            quote = QUOTE,
            signedTransactions = SIGNED_TXNS,
            unsignedTransactions = UNSIGNED_TXNS,
            localAddresses = listOf(LOCAL_ADDRESS_1, LOCAL_ADDRESS_2)
        )
    }
}
