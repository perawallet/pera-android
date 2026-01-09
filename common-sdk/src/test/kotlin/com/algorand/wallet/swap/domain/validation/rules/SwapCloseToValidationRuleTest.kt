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
import org.junit.Test

class SwapCloseToValidationRuleTest {

    private val getParsedSwapTransactions: GetParsedSwapTransactions = mockk()

    private val sut = SwapCloseToValidationRule(getParsedSwapTransactions)

    @Test
    fun `EXPECT false WHEN there is closeTo address and sender is local address`() {
        val invalidTransaction = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(LOCAL_ADDRESS, null),
            closeToAddress = AlgorandAddress("some address", null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(invalidTransaction)

        val result = sut(VALIDATION_DATA)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN there is assetCloseTo address and sender is local address`() {
        val invalidTransaction = VALID_TRANSACTION.copy(
            senderAddress = AlgorandAddress(LOCAL_ADDRESS, null),
            assetCloseToAddress = AlgorandAddress("some address", null)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(invalidTransaction)

        val result = sut(VALIDATION_DATA)

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN there is no closeTo and assetCloseTo address`() {
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(VALID_TRANSACTION)

        val result = sut(VALIDATION_DATA)

        assert(result)
    }

    private companion object {
        const val LOCAL_ADDRESS = "LOCAL_ADDRESS_1"
        val VALID_TRANSACTION = SwapValidationRuleTransactionBuilder.buildEmptyTransaction()
        val SIGNED_TXNS: List<ByteArray?> = peraFixture()
        val UNSIGNED_TXNS: List<ByteArray?> = peraFixture()
        val VALIDATION_DATA = SwapTransactionValidationData(
            quote = peraFixture(),
            signedTransactions = SIGNED_TXNS,
            unsignedTransactions = UNSIGNED_TXNS,
            localAddresses = listOf(LOCAL_ADDRESS)
        )
    }
}
