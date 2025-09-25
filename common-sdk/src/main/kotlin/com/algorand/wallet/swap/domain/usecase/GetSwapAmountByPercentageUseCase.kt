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
import com.algorand.wallet.account.lite.domain.usecase.GetAssetHoldingsLite
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.model.InsufficientAlgoBalanceException
import com.algorand.wallet.swap.domain.model.SwapAmountByPercentagePayload
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

internal class GetSwapAmountByPercentageUseCase @Inject constructor(
    private val getAccountMinBalance: GetAccountMinBalance,
    private val getAccountAssetHolding: GetAssetHoldingsLite,
    private val getSwapPeraFee: GetSwapPeraFee,
    private val getSelectedSwapAssetDetail: GetSelectedSwapAssetDetail,
    private val getSwapFeePadding: GetSwapFeePadding
) : GetSwapAmountByPercentage {

    override suspend fun invoke(payload: SwapAmountByPercentagePayload): PeraResult<BigDecimal> {
        val isAssetInAlgo = payload.assetInId == ALGO_ID
        val (minRequiredBalance, accountAlgoBalance) = getMinBalanceAndAccountAlgoBalancePair(payload.address)
        return if (isAssetInAlgo) {
            getBalancePercentageForAlgo(accountAlgoBalance, minRequiredBalance, payload)
        } else {
            getBalancePercentageForAsset(accountAlgoBalance, minRequiredBalance, payload)
        }
    }

    private suspend fun getBalancePercentageForAlgo(
        algoBalance: BigDecimal,
        minRequiredBalance: BigDecimal,
        payload: SwapAmountByPercentagePayload
    ): PeraResult<BigDecimal> {
        val percentage = payload.percentage.toBigDecimal()
        val percentageCalculatedAlgoAmount = algoBalance.multiply(percentage).divide(percentageDivider)
        return getSwapPeraFee(ALGO_ID, percentageCalculatedAlgoAmount, ALGO_DECIMALS).use(
            onSuccess = { peraFee ->
                val remainingBalance = algoBalance.minus(minRequiredBalance).minus(getSwapFeePadding()).minus(peraFee)
                when {
                    remainingBalance < BigDecimal.ZERO -> PeraResult.Error(InsufficientAlgoBalanceException())
                    remainingBalance < percentageCalculatedAlgoAmount -> PeraResult.Success(remainingBalance)
                    else -> PeraResult.Success(percentageCalculatedAlgoAmount)
                }
            },
            onFailed = { exception, _ -> PeraResult.Error(exception) }
        )
    }

    private suspend fun getBalancePercentageForAsset(
        algoBalance: BigDecimal,
        minRequiredBalance: BigDecimal,
        payload: SwapAmountByPercentagePayload
    ): PeraResult<BigDecimal> {
        val remainingBalanceAfterFees = algoBalance.minus(minRequiredBalance).minus(getSwapFeePadding())
        return if (remainingBalanceAfterFees < BigDecimal.ZERO) {
            PeraResult.Error(InsufficientAlgoBalanceException())
        } else {
            getSelectedSwapAssetDetail(payload.address, payload.assetInId).use(
                onSuccess = { assetDetail ->
                    val percentageCalculatedBalance = getBalanceByPercentage(assetDetail, payload)
                    if (payload.assetOutId == ALGO_ID) {
                        PeraResult.Success(percentageCalculatedBalance)
                    } else {
                        getSwapPeraFee(payload.assetInId, percentageCalculatedBalance, assetDetail.decimal).use(
                            onSuccess = { peraFee ->
                                val peraFeeDeductedAmount = remainingBalanceAfterFees.minus(peraFee)
                                if (peraFeeDeductedAmount < BigDecimal.ZERO) {
                                    PeraResult.Error(InsufficientAlgoBalanceException())
                                } else {
                                    PeraResult.Success(percentageCalculatedBalance)
                                }
                            },
                            onFailed = { exception, i ->
                                PeraResult.Error(exception)
                            }
                        )
                    }
                },
                onFailed = { exception, i ->
                    PeraResult.Error(exception)
                }
            )
        }
    }

    private fun getBalanceByPercentage(
        assetDetail: SwapSelectedAssetDetail,
        payload: SwapAmountByPercentagePayload
    ): BigDecimal {
        return assetDetail.amount
            .toBigDecimal()
            .movePointLeft(assetDetail.decimal)
            .multiply(payload.percentage.toBigDecimal())
            .divide(percentageDivider, assetDetail.decimal, RoundingMode.DOWN)
    }

    private suspend fun getMinBalanceAndAccountAlgoBalancePair(accountAddress: String): Pair<BigDecimal, BigDecimal> {
        val accountMinBalance = getAccountMinBalance(accountAddress).toBigDecimal().movePointLeft(ALGO_DECIMALS)
        val accountBalance = getAccountAssetHolding(accountAddress, listOf(ALGO_ID))
            .assetHoldingAmounts[ALGO_ID]?.toBigDecimal()?.movePointLeft(ALGO_DECIMALS)
            ?: return BigDecimal.ZERO to BigDecimal.ZERO

        return accountMinBalance to accountBalance
    }

    private companion object {
        val percentageDivider = BigDecimal.valueOf(100L)
    }
}
