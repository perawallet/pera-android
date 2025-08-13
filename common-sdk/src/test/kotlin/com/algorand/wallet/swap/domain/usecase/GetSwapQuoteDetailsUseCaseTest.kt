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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalance
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapQuote
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail.SwapQuoteState.NonSwappable
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAlgoBalance
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAssetBalance
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientBalanceForFee
import io.mockk.coEvery
import io.mockk.mockk
import java.math.BigDecimal
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSwapQuoteDetailsUseCaseTest {

    private val getAccountAssetHolding: GetAccountAssetHolding = mockk(relaxed = true)
    private val getAccountMinBalance: GetAccountMinBalance = mockk(relaxed = true)

    private val sut = GetSwapQuoteDetailsUseCase(getAccountAssetHolding, getAccountMinBalance)

    @Test
    fun `EXPECT insufficient algo balance WHEN account does not have enough algo for txn`() = runTest {
        val quote = QUOTE.copy(
            assetInAmount = QUOTE.assetInAmount.copy(amount = BigDecimal.TEN),
            assetInDetail = QUOTE.assetInDetail.copy(assetId = ALGO_ID, fractionDecimals = ALGO_DECIMALS)
        )
        coEvery { getAccountAssetHolding(ADDRESS, ALGO_ID) } returns ASSET_HOLDING.copy(amount = BigInteger.ONE)

        val result = sut(listOf(quote))

        val expected = SwapQuoteDetail(quote, NonSwappable(InsufficientAlgoBalance))
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `EXPECT insufficient asset balance WHEN account does not have enough asset for txn`() = runTest {
        val quote = QUOTE.copy(
            assetInAmount = QUOTE.assetInAmount.copy(amount = BigDecimal.TEN),
            assetInDetail = QUOTE.assetInDetail.copy(assetId = ASSET_ID, fractionDecimals = 0, shortName = "NAME")
        )
        coEvery { getAccountAssetHolding(ADDRESS, ASSET_ID) } returns ASSET_HOLDING.copy(amount = BigInteger.ZERO)

        val result = sut(listOf(quote))

        val expected = SwapQuoteDetail(quote, NonSwappable(InsufficientAssetBalance("NAME")))
        assertEquals(listOf(expected), result)
    }

    @Test
    fun `EXPECT insufficient balance to pay fees WHEN asset in is algo and account does not have enough algo for fees and txn`() =
        runTest {
            val accountBalance = BigInteger.valueOf(4_664_000) // 4 ALGO + 0.664 ALGO fee padding
            val quote = QUOTE.copy(
                fee = QUOTE.fee.copy(peraFeeAmount = BigDecimal.ONE),
                assetInDetail = QUOTE.assetInDetail.copy(assetId = ALGO_ID, fractionDecimals = ALGO_DECIMALS),
                assetInAmount = QUOTE.assetInAmount.copy(amount = BigDecimal.valueOf(2))
            )
            coEvery { getAccountAssetHolding(ADDRESS, ALGO_ID) } returns ASSET_HOLDING.copy(amount = accountBalance)
            coEvery { getAccountMinBalance(ADDRESS) } returns BigInteger.valueOf(1_000_000)

            val result = sut(listOf(quote))

            val expectedMinBalance = BigDecimal.valueOf(1_000_000, ALGO_DECIMALS)
            val expected = SwapQuoteDetail(quote, NonSwappable(InsufficientBalanceForFee(expectedMinBalance)))
            assertEquals(listOf(expected), result)
        }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val ASSET_ID = peraFixture<Long>()
        val ASSET_HOLDING = peraFixture<AssetHolding>()
        val QUOTE = peraFixture<SwapQuote>().copy(
            accountAddress = ADDRESS
        )
    }
}
