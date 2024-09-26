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

package com.algorand.android.module.swap.component.domain.usecase

import com.algorand.android.assetutils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.assetutils.AssetConstants.ALGO_ID
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.foundation.PeraResult
import com.algorand.android.foundation.common.isLesserThan
import com.algorand.android.module.swap.component.data.SWAP_FEE_PADDING
import com.algorand.android.module.swap.component.domain.exceptions.InsufficientAlgoBalance
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

internal class GetBalancePercentageForAssetUseCase @Inject constructor(
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getSwapPeraFee: GetSwapPeraFee
) : GetBalancePercentageForAsset {

    override suspend fun invoke(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        accountAddress: String,
        fromAssetId: Long,
        percentage: BigDecimal,
        toAssetId: Long
    ): PeraResult<BigDecimal> {
        val remainingAlgoBalance = getRemainingAlgoBalance(accountAlgoBalance, minRequiredBalance)

        return when {
            remainingAlgoBalance isLesserThan ZERO -> PeraResult.Error(InsufficientAlgoBalance)
            toAssetId == ALGO_ID -> {
                val calculatedBalance = getPercentageCalculatedBalance(accountAddress, fromAssetId, percentage)
                PeraResult.Success(calculatedBalance)
            }
            else -> {
                getFeeDeductedAmount(accountAddress, fromAssetId, remainingAlgoBalance, percentage)
            }
        }
    }

    private suspend fun getFeeDeductedAmount(
        address: String,
        fromAssetId: Long,
        calculatedAlgoBalance: BigDecimal,
        percentage: BigDecimal
    ): PeraResult<BigDecimal> {
        val assetData = getAccountAssetData(address, fromAssetId) ?: return PeraResult.Error(IllegalArgumentException())
        val assetDecimal = assetData.decimals
        val percentageCalculatedBalance = getPercentageCalculatedBalance(address, fromAssetId, percentage)
        return getSwapPeraFee(fromAssetId, percentageCalculatedBalance, assetDecimal).use(
            onSuccess = {
                val feeDeductedAmount = calculatedAlgoBalance.minus(it.peraFeeAmount)
                if (feeDeductedAmount isLesserThan ZERO) {
                    PeraResult.Error(InsufficientAlgoBalance)
                } else {
                    PeraResult.Success(percentageCalculatedBalance)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    private fun getRemainingAlgoBalance(accountAlgoBalance: BigInteger, minRequiredBalance: BigInteger): BigDecimal {
        return accountAlgoBalance
            .minus(minRequiredBalance)
            .minus(SWAP_FEE_PADDING.toBigInteger())
            .toBigDecimal()
            .movePointLeft(ALGO_DECIMALS)
    }

    private suspend fun getPercentageCalculatedBalance(
        address: String,
        fromAssetId: Long,
        percentage: BigDecimal
    ): BigDecimal {
        val assetData = getAccountAssetData(address, fromAssetId) ?: return ZERO
        return assetData.amount
            .toBigDecimal()
            .movePointLeft(assetData.decimals)
            .multiply(percentage)
            .divide(PERCENTAGE_DIVIDER, assetData.decimals, RoundingMode.DOWN)
    }

    private suspend fun getAccountAssetData(address: String, fromAssetId: Long): OwnedAssetData? {
        return getAccountOwnedAssetData(address, fromAssetId, includeAlgo = false)
    }

    private companion object {
        val PERCENTAGE_DIVIDER = BigDecimal(100)
    }
}
