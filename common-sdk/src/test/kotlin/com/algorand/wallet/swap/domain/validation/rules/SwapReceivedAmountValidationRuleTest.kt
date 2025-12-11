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
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.usecase.GetParsedSwapTransactions
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class SwapReceivedAmountValidationRuleTest {

    private val getParsedSwapTransactions: GetParsedSwapTransactions = mockk()

    private val sut = SwapReceivedAmountValidationRule(getParsedSwapTransactions)

    @Test
    fun `EXPECT true WHEN received ALGO amount equals expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = expectedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN received ALGO amount is greater than expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val receivedAmount = BigInteger.valueOf(1001)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = receivedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN received ALGO amount is less than expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val receivedAmount = BigInteger.valueOf(999)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = receivedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN received asset amount equals expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN received asset amount is greater than expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val receivedAmount = BigInteger.valueOf(1001)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = receivedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN received asset amount is less than expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val receivedAmount = BigInteger.valueOf(999)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = receivedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN multiple ALGO transactions sum to expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction1 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = BigInteger.valueOf(600).toString()
        )
        val transaction2 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = BigInteger.valueOf(400).toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN multiple asset transactions sum to expected min amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction1 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = BigInteger.valueOf(600)
        )
        val transaction2 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = BigInteger.valueOf(400)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN ALGO transaction receiver does not match account address`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress("DIFFERENT_ADDRESS", null),
            amount = expectedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN asset transaction receiver does not match account address`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress("DIFFERENT_ADDRESS", null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN asset transaction assetId does not match`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = 456L,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN ALGO transaction type is not PAY_TRANSACTION`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = expectedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN asset transaction type is not ASSET_TRANSACTION`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN ALGO transaction amount is null`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoOutQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            receiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = null
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN asset transaction assetAmount is null`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetOutQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            assetReceiverAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = null
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    private fun createAlgoOutQuote(expectedMinAmount: BigInteger): SwapQuoteV2 {
        val amount = BigDecimal(expectedMinAmount).movePointLeft(6)
        return peraFixture<SwapQuoteV2>().copy(
            accountAddress = ACCOUNT_ADDRESS,
            assetOutDetail = peraFixture<SwapQuoteV2.AssetDetail>().copy(
                assetId = ALGO_ID,
                fractionDecimals = 6
            ),
            assetOutAmount = SwapQuoteV2.AssetAmount(
                amount = BigDecimal.ZERO,
                amountInUsdValue = BigDecimal.ZERO,
                amountWithSlippage = amount
            )
        )
    }

    private fun createAssetOutQuote(expectedMinAmount: BigInteger, assetId: Long): SwapQuoteV2 {
        val amount = BigDecimal(expectedMinAmount).movePointLeft(6)
        return peraFixture<SwapQuoteV2>().copy(
            accountAddress = ACCOUNT_ADDRESS,
            assetOutDetail = peraFixture<SwapQuoteV2.AssetDetail>().copy(
                assetId = assetId,
                fractionDecimals = 6
            ),
            assetOutAmount = SwapQuoteV2.AssetAmount(
                amount = BigDecimal.ZERO,
                amountInUsdValue = BigDecimal.ZERO,
                amountWithSlippage = amount
            )
        )
    }

    private fun createValidationData(quote: SwapQuoteV2): SwapTransactionValidationData {
        return SwapTransactionValidationData(
            quote = quote,
            signedTransactions = SIGNED_TXNS,
            unsignedTransactions = UNSIGNED_TXNS,
            localAddresses = emptyList()
        )
    }

    private companion object {
        const val ACCOUNT_ADDRESS = "ACCOUNT_ADDRESS"
        val VALID_TRANSACTION = SwapValidationRuleTransactionBuilder.buildEmptyTransaction()
        val SIGNED_TXNS: List<ByteArray?> = peraFixture()
        val UNSIGNED_TXNS: List<ByteArray?> = peraFixture()
    }
}

