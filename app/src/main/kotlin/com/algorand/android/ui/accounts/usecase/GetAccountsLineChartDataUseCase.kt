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

package com.algorand.android.ui.accounts.usecase

import com.algorand.android.modules.parity.domain.usecase.GetPrimaryAlgoParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryAlgoParityValue
import com.algorand.android.ui.accounts.model.AccountsLineChartData
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.android.utils.ALGO_DECIMALS
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import com.algorand.wallet.wealth.wallet.domain.usecase.GetWalletWealth
import javax.inject.Inject

internal class GetAccountsLineChartDataUseCase @Inject constructor(
    private val getWalletWealth: GetWalletWealth,
    private val getPrimaryAlgoParityValue: GetPrimaryAlgoParityValue,
    private val getSecondaryAlgoParityValue: GetSecondaryAlgoParityValue,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer
) : GetAccountsLineChartData {

    override suspend fun invoke(
        addresses: List<String>,
        period: WalletWealthPeriod
    ): PeraResult<List<AccountsLineChartData>> {
        return getWalletWealth(addresses, period).map { walletWealth ->
            walletWealth.chartData.map { chartData ->
                val algoAmount = chartData.algoValue.movePointRight(ALGO_DECIMALS).toBigInteger()
                val primaryAmount = PeraAmount(getPrimaryAlgoParityValue(algoAmount).amountAsCurrency)
                val secondaryAmount = PeraAmount(getSecondaryAlgoParityValue(algoAmount).amountAsCurrency)
                val primaryRenderer = getCompactPrimaryAmountRenderer(primaryAmount, Plain)
                val secondaryRenderer = getCompactSecondaryAmountRenderer(secondaryAmount, Plain)
                AccountsLineChartData(
                    datetime = chartData.datetime,
                    primaryValue = primaryAmount.value,
                    primaryAmountRenderer = primaryRenderer,
                    secondaryAmountRenderer = secondaryRenderer,
                    round = chartData.round
                )
            }
        }
    }
}
