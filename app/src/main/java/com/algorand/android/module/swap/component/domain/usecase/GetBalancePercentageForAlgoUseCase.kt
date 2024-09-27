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

import com.algorand.android.module.asset.utils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.module.asset.utils.AssetConstants.ALGO_ID
import com.algorand.android.foundation.PeraResult
import com.algorand.android.foundation.PeraResult.Error
import com.algorand.android.foundation.PeraResult.Success
import com.algorand.android.foundation.common.isLesserThan
import com.algorand.android.module.swap.component.data.SWAP_FEE_PADDING
import com.algorand.android.module.swap.component.domain.exceptions.InsufficientAlgoBalance
import com.algorand.android.module.swap.component.domain.model.PeraFee
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class GetBalancePercentageForAlgoUseCase @Inject constructor(
    private val getSwapPeraFee: GetSwapPeraFee
) : GetBalancePercentageForAlgo {

    override suspend fun invoke(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        percentage: BigDecimal
    ): PeraResult<BigDecimal> {
        val percentageCalculatedAlgoAmount = getPercentageCalculatedAlgoAmount(accountAlgoBalance, percentage)
        return getSwapPeraFee(ALGO_ID, percentageCalculatedAlgoAmount, ALGO_DECIMALS).use(
            onSuccess = { peraFee ->
                getBalancePercentage(accountAlgoBalance, minRequiredBalance, peraFee, percentage)
            },
            onFailed = { exception, code ->
                Error(exception, code)
            }
        )
    }

    private fun getBalancePercentage(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        peraFee: PeraFee,
        percentage: BigDecimal
    ): PeraResult<BigDecimal> {
        val remainingBalance = getRequiredBalancesDeductedAccountBalance(
            accountAlgoBalance = accountAlgoBalance,
            minRequiredBalance = minRequiredBalance,
            peraFee = peraFee
        )
        val percentageCalculatedAlgoAmount = getPercentageCalculatedAlgoAmount(accountAlgoBalance, percentage)
        return when {
            remainingBalance isLesserThan BigDecimal.ZERO -> Error(InsufficientAlgoBalance)
            remainingBalance isLesserThan percentageCalculatedAlgoAmount -> Success(remainingBalance)
            else -> Success(percentageCalculatedAlgoAmount)
        }
    }

    private fun getPercentageCalculatedAlgoAmount(accountAlgoBalance: BigInteger, percentage: BigDecimal): BigDecimal {
        return accountAlgoBalance
            .toBigDecimal()
            .movePointLeft(ALGO_DECIMALS)
            .multiply(percentage)
            .divide(PERCENTAGE_DIVIDER)
    }

    private fun getRequiredBalancesDeductedAccountBalance(
        accountAlgoBalance: BigInteger,
        minRequiredBalance: BigInteger,
        peraFee: PeraFee
    ): BigDecimal {
        return accountAlgoBalance.toBigDecimal()
            .movePointLeft(ALGO_DECIMALS)
            .minus(minRequiredBalance.toBigDecimal().movePointLeft(ALGO_DECIMALS))
            .minus(SWAP_FEE_PADDING)
            .minus(peraFee.peraFeeAmount)
    }

    private companion object {
        private val PERCENTAGE_DIVIDER = BigDecimal.valueOf(100L)
    }
}
