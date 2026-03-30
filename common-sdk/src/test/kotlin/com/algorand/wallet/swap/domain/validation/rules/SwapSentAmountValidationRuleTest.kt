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
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwapSentAmountValidationRuleTest {

    private val getParsedSwapTransactions: GetParsedSwapTransactions = mockk()

    private val sut = SwapSentAmountValidationRule(getParsedSwapTransactions)

    @Test
    fun `EXPECT true WHEN sent ALGO amount equals expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = expectedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN sent ALGO amount is less than expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val sentAmount = BigInteger.valueOf(999)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = sentAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN sent ALGO amount is greater than expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val sentAmount = BigInteger.valueOf(1001)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = sentAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN sent asset amount equals expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN sent asset amount is less than expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val sentAmount = BigInteger.valueOf(999)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = sentAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN sent asset amount is greater than expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val sentAmount = BigInteger.valueOf(1001)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = sentAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN multiple ALGO transactions sum to expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction1 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = BigInteger.valueOf(600).toString()
        )
        val transaction2 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = BigInteger.valueOf(400).toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN multiple asset transactions sum to expected max amount`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction1 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = BigInteger.valueOf(600)
        )
        val transaction2 = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = BigInteger.valueOf(400)
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction1, transaction2)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN ALGO transaction sender does not match account address`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress("DIFFERENT_ADDRESS", null),
            amount = expectedAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN asset transaction sender does not match account address`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress("DIFFERENT_ADDRESS", null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN ALGO transaction amount is null`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val quote = createAlgoInQuote(expectedAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = null
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN asset transaction assetAmount is null`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val quote = createAssetInQuote(expectedAmount, assetId)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = null
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN sent ALGO amount plus fee equals expected max amount`() {
        val baseAmount = BigInteger.valueOf(1000)
        val feeAmount = BigInteger.valueOf(100)
        val quote = createAlgoInQuote(baseAmount, feeAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = baseAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN sent ALGO amount plus fee is less than expected max amount`() {
        val baseAmount = BigInteger.valueOf(1000)
        val feeAmount = BigInteger.valueOf(100)
        val sentAmount = BigInteger.valueOf(900)
        val quote = createAlgoInQuote(baseAmount, feeAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = sentAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN sent ALGO amount plus fee exceeds expected max amount`() {
        val baseAmount = BigInteger.valueOf(1000)
        val feeAmount = BigInteger.valueOf(100)
        val sentAmount = BigInteger.valueOf(1101)
        val quote = createAlgoInQuote(baseAmount, feeAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.PAY_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            amount = sentAmount.toString()
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertFalse(result)
    }

    @Test
    fun `EXPECT true WHEN asset in is not ALGO fee should not be added`() {
        val expectedAmount = BigInteger.valueOf(1000)
        val assetId = 123L
        val feeAmount = BigInteger.valueOf(100)
        val quote = createAssetInQuote(expectedAmount, assetId, feeAmount)
        val transaction = VALID_TRANSACTION.copy(
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            senderAddress = AlgorandAddress(ACCOUNT_ADDRESS, null),
            assetId = assetId,
            assetAmount = expectedAmount
        )
        every { getParsedSwapTransactions(SIGNED_TXNS, UNSIGNED_TXNS) } returns listOf(transaction)

        val result = sut(createValidationData(quote))

        assertTrue(result)
    }

    private fun createAlgoInQuote(expectedMaxAmount: BigInteger, feeAmount: BigInteger = BigInteger.ZERO): SwapQuoteV2 {
        val amount = BigDecimal(expectedMaxAmount).movePointLeft(6)
        val fee = BigDecimal(feeAmount).movePointLeft(6)
        return peraFixture<SwapQuoteV2>().copy(
            accountAddress = ACCOUNT_ADDRESS,
            assetInDetail = peraFixture<SwapQuoteV2.AssetDetail>().copy(
                assetId = ALGO_ID,
                fractionDecimals = 6
            ),
            assetInAmount = SwapQuoteV2.AssetAmount(
                amount = BigDecimal.ZERO,
                amountInUsdValue = BigDecimal.ZERO,
                amountWithSlippage = amount
            ),
            fee = SwapQuoteV2.SwapFee(
                peraFeeAmountInAlgo = fee,
                type = SwapQuoteV2.SwapFee.PeraFeeType.Algo
            )
        )
    }

    private fun createAssetInQuote(
        expectedMaxAmount: BigInteger,
        assetId: Long,
        feeAmount: BigInteger = BigInteger.ZERO
    ): SwapQuoteV2 {
        val amount = BigDecimal(expectedMaxAmount).movePointLeft(6)
        val fee = BigDecimal(feeAmount).movePointLeft(6)
        return peraFixture<SwapQuoteV2>().copy(
            accountAddress = ACCOUNT_ADDRESS,
            assetInDetail = peraFixture<SwapQuoteV2.AssetDetail>().copy(
                assetId = assetId,
                fractionDecimals = 6
            ),
            assetInAmount = SwapQuoteV2.AssetAmount(
                amount = BigDecimal.ZERO,
                amountInUsdValue = BigDecimal.ZERO,
                amountWithSlippage = amount
            ),
            fee = SwapQuoteV2.SwapFee(
                peraFeeAmountInAlgo = fee,
                type = SwapQuoteV2.SwapFee.PeraFeeType.Algo
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
