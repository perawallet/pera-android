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

import com.algorand.wallet.account.core.domain.usecase.GetAccountMinBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapQuoteDetail
import com.algorand.wallet.swap.domain.model.SwapQuoteException
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAlgoBalance
import com.algorand.wallet.swap.domain.model.SwapQuoteException.InsufficientAssetBalance
import com.algorand.wallet.swap.domain.utils.swapFeePadding
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class GetSwapQuoteDetailsUseCase @Inject constructor(
    private val getAccountAssetHolding: GetAccountAssetHolding,
    private val getAccountMinBalance: GetAccountMinBalance
) : GetSwapQuoteDetails {

    override suspend fun invoke(quotes: List<SwapQuoteV2>): List<SwapQuoteDetail> {
        return quotes.map { quote ->
            when {
                !hasAccountEnoughBalanceToCompleteSwap(quote) -> getInsufficientBalanceState(quote)
                !hasAccountEnoughBalanceToPayFees(quote) -> getInsufficientBalanceForFeeState(quote)
                else -> SwapQuoteDetail(quote, SwapQuoteDetail.SwapQuoteState.Swappable)
            }
        }
    }

    private suspend fun hasAccountEnoughBalanceToCompleteSwap(quote: SwapQuoteV2): Boolean {
        val assetInAmount = quote.assetInAmount.amount
        val userBalance = getUserBalance(quote.accountAddress, quote.assetInDetail)
        return assetInAmount <= userBalance
    }

    private suspend fun hasAccountEnoughBalanceToPayFees(quote: SwapQuoteV2): Boolean {
        with(quote) {
            val userAlgoBalance = getAlgoBalance(accountAddress)
            val minRequiredBalance = getMinRequiredBalance(accountAddress)
            val requiredBalance = when {
                isAssetInAlgo -> assetInAmount.amount.add(minRequiredBalance)
                isAssetOutAlgo -> minRequiredBalance.minus(assetOutAmount.amountWithSlippage)
                else -> minRequiredBalance
            }.add(fee.peraFeeAmount).add(swapFeePadding)
            return requiredBalance <= userAlgoBalance
        }
    }

    private suspend fun getUserBalance(address: String, assetDetail: SwapQuoteV2.AssetDetail): BigDecimal {
        val assetHolding = getAccountAssetHolding(address, assetDetail.assetId) ?: return BigDecimal.ZERO
        return assetHolding.amount.toBigDecimal().movePointLeft(assetDetail.fractionDecimals)
    }

    private fun getInsufficientBalanceState(quote: SwapQuoteV2): SwapQuoteDetail {
        val exception = with(quote) {
            if (isAssetInAlgo) InsufficientAlgoBalance else InsufficientAssetBalance(assetInDetail.shortName)
        }
        return SwapQuoteDetail(quote, SwapQuoteDetail.SwapQuoteState.NonSwappable(exception))
    }

    private suspend fun getInsufficientBalanceForFeeState(quote: SwapQuoteV2): SwapQuoteDetail {
        val minRequiredBalance = getAccountMinBalance(quote.accountAddress).toBigDecimal().movePointLeft(ALGO_DECIMALS)
        val exception = SwapQuoteException.InsufficientBalanceForFee(minRequiredBalance)
        return SwapQuoteDetail(quote, SwapQuoteDetail.SwapQuoteState.NonSwappable(exception))
    }

    private suspend fun getMinRequiredBalance(address: String): BigDecimal {
        return getAccountMinBalance(address).toBigDecimal().movePointLeft(ALGO_DECIMALS)
    }

    private suspend fun getAlgoBalance(address: String): BigDecimal {
        val algoBalance = getAccountAssetHolding(address, ALGO_ID)?.amount ?: BigInteger.ZERO
        return algoBalance.toBigDecimal().movePointLeft(ALGO_DECIMALS)
    }
}
