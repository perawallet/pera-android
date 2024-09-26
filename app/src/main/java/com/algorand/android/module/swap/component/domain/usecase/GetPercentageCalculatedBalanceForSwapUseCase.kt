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

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetutils.AssetConstants.ALGO_ID
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.foundation.PeraResult
import com.algorand.android.module.swap.component.domain.model.GetPercentageCalculatedBalanceForSwapPayload
import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ZERO
import javax.inject.Inject

internal class GetPercentageCalculatedBalanceForSwapUseCase @Inject constructor(
    private val getBalancePercentageForAlgo: GetBalancePercentageForAlgo,
    private val getBalancePercentageForAsset: GetBalancePercentageForAsset,
    private val getAccountMinBalance: GetAccountMinBalance,
    private val getAccountInformation: GetAccountInformation
) : GetPercentageCalculatedBalanceForSwap {

    override suspend fun invoke(payload: GetPercentageCalculatedBalanceForSwapPayload): PeraResult<BigDecimal> {
        val (minRequiredBalance, accountAlgoBalance) = getMinBalanceAndAccountAlgoBalancePair(payload.accountAddress)
        val percentage = payload.percentage.toBigDecimal()
        return if (payload.isFromAssetAlgo()) {
            getBalancePercentageForAlgo(accountAlgoBalance, minRequiredBalance, percentage)
        } else {
            getBalancePercentageForAsset(
                accountAlgoBalance = accountAlgoBalance,
                minRequiredBalance = minRequiredBalance,
                accountAddress = payload.accountAddress,
                fromAssetId = payload.fromAssetId,
                percentage = percentage,
                toAssetId = payload.toAssetId
            )
        }
    }

    private suspend fun getMinBalanceAndAccountAlgoBalancePair(accountAddress: String): Pair<BigInteger, BigInteger> {
        val accountInformation = getAccountInformation(accountAddress) ?: return ZERO to ZERO
        val minRequiredBalance = getAccountMinBalance(accountInformation)
        return minRequiredBalance to accountInformation.amount
    }

    private fun GetPercentageCalculatedBalanceForSwapPayload.isFromAssetAlgo(): Boolean {
        return fromAssetId == ALGO_ID
    }
}
