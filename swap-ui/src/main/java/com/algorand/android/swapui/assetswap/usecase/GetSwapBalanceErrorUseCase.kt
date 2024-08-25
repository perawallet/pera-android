/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.swapui.assetswap.usecase

import android.content.Context
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_DECIMALS
import com.algorand.android.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.getXmlStyledString
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.common.isEqualTo
import com.algorand.android.foundation.common.isLesserThan
import com.algorand.android.swap.data.SWAP_FEE_PADDING
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swapui.assetswap.model.SwapError
import java.math.BigDecimal

internal class GetSwapBalanceErrorUseCase(
    private val context: Context,
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getAccountMinBalance: GetAccountMinBalance
) : GetSwapBalanceError {

    override suspend fun invoke(swapQuote: SwapQuote, accountAddress: String): Event<SwapError>? {
        return when {
            !hasAccountEnoughBalanceToCompleteSwap(swapQuote, accountAddress) -> {
                val error = if (swapQuote.fromAssetDetail.assetId == ALGO_ASSET_ID) {
                    SwapError(description = context.getString(R.string.account_does_not_have))
                } else {
                    getSafeAsaInsufficientBalanceErrorResource(swapQuote)
                }
                Event(error)
            }
            !hasAccountEnoughBalanceToPayFees(swapQuote, accountAddress) -> {
                val formattedMinimumBalance = getUserMinRequiredBalance(accountAddress)
                    .stripTrailingZeros()
                    .toPlainString()
                val errorAnnotatedString = AnnotatedString(
                    stringResId = R.string.algo_balance_is_too_low,
                    replacementList = listOf(
                        "algo_icon" to Currency.ALGO.symbol,
                        "min_balance" to formattedMinimumBalance
                    )
                )
                val error = SwapError(description = context.getXmlStyledString(errorAnnotatedString).toString())
                Event(error)
            }
            else -> null
        }
    }

    private suspend fun hasAccountEnoughBalanceToCompleteSwap(swapQuote: SwapQuote, accountAddress: String): Boolean {
        val fromAssetAmount = swapQuote.fromAssetAmount.movePointLeft(swapQuote.fromAssetDetail.fractionDecimals)
        val userBalance = getUserBalance(swapQuote.fromAssetDetail.assetId, accountAddress)
        return fromAssetAmount isLesserThan userBalance || fromAssetAmount isEqualTo userBalance
    }

    private suspend fun hasAccountEnoughBalanceToPayFees(swapQuote: SwapQuote, accountAddress: String): Boolean {
        with(swapQuote) {
            val userAlgoBalance = getUserBalance(ALGO_ASSET_ID, accountAddress)
            val minBalanceUserNeedsToKeep = getUserMinRequiredBalance(accountAddress)

            val requiredBalance = when {
                isFromAssetAlgo -> {
                    fromAssetAmount.movePointLeft(ALGO_DECIMALS)
                        .add(minBalanceUserNeedsToKeep)
                }
                isToAssetAlgo -> {
                    val incomingAlgoAmount = swapQuote.toAssetAmountWithSlippage.movePointLeft(ALGO_DECIMALS)
                    minBalanceUserNeedsToKeep.minus(incomingAlgoAmount)
                }
                else -> {
                    minBalanceUserNeedsToKeep
                }
            }.add(peraFeeAmount).add(SWAP_FEE_PADDING)

            return requiredBalance isLesserThan userAlgoBalance || requiredBalance isEqualTo userAlgoBalance
        }
    }

    private suspend fun getUserMinRequiredBalance(accountAddress: String): BigDecimal {
        return getAccountMinBalance(accountAddress).toBigDecimal().movePointLeft(ALGO_DECIMALS)
    }

    private suspend fun getUserBalance(assetId: Long, accountAddress: String): BigDecimal {
        val ownedAssetData = getAccountOwnedAssetData(accountAddress, assetId, includeAlgo = true)
        return ownedAssetData?.amount?.toBigDecimal()?.movePointLeft(ownedAssetData.decimals) ?: BigDecimal.ZERO
    }

    private fun getSafeAsaInsufficientBalanceErrorResource(swapQuote: SwapQuote): SwapError {
        val asaShortName = swapQuote.fromAssetDetail.shortName
        return if (asaShortName.isBlank()) {
            SwapError(description = context.getString(R.string.asa_balance_is_not_sufficient), title = null)
        } else {
            val asaShortNamePair = "asa_short_name" to asaShortName
            val annotatedString = AnnotatedString(
                stringResId = R.string.asa_balance_is_not_sufficient_formatted,
                replacementList = listOf(asaShortNamePair)
            )
            val description = context.getXmlStyledString(annotatedString).toString()
            SwapError(description = description)
        }
    }
}
