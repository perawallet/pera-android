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

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryFiatCurrencyId
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.ui.accounts.model.AccountsLineChartData
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType.Plain
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.wealth.wallet.domain.model.WalletWealthPeriod
import com.algorand.wallet.wealth.wallet.domain.usecase.GetWalletWealth
import javax.inject.Inject

internal class GetAccountsLineChartDataUseCase @Inject constructor(
    private val getWalletWealth: GetWalletWealth,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer,
    private val getPrimaryFiatCurrencyId: GetPrimaryFiatCurrencyId,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetAccountsLineChartData {

    override suspend fun invoke(
        addresses: List<String>,
        period: WalletWealthPeriod
    ): PeraResult<List<AccountsLineChartData>> {
        val currency = getPrimaryFiatCurrencyId()
        return getWalletWealth(addresses, period, currency).map { walletWealth ->
            walletWealth.chartData.map { chartData ->
                val primaryAmount: PeraAmount
                val secondaryAmount: PeraAmount
                if (isPrimaryCurrencyAlgo()) {
                    primaryAmount = PeraAmount(
                        ParityValue(
                            amountAsCurrency = chartData.algoValue,
                            selectedCurrencySymbol = Currency.ALGO.symbol
                        ).amountAsCurrency
                    )
                    secondaryAmount = PeraAmount(
                        ParityValue(
                            amountAsCurrency = chartData.usdValue,
                            selectedCurrencySymbol = Currency.USD.symbol
                        ).amountAsCurrency
                    )
                } else {
                    primaryAmount = PeraAmount(
                        ParityValue(
                            amountAsCurrency = chartData.valueInCurrency,
                            selectedCurrencySymbol = getPrimaryCurrencySymbol().orEmpty()
                        ).amountAsCurrency
                    )
                    secondaryAmount =
                        PeraAmount(
                            ParityValue(
                                amountAsCurrency = chartData.algoValue,
                                selectedCurrencySymbol = Currency.ALGO.symbol
                            ).amountAsCurrency
                        )
                }
                val primaryRenderer = getCompactPrimaryAmountRenderer(primaryAmount, Plain)
                val secondaryRenderer = getCompactSecondaryAmountRenderer(secondaryAmount, Plain)
                AccountsLineChartData(
                    datetime = chartData.datetime,
                    primaryValue = primaryAmount.value,
                    valueInCurrency = chartData.valueInCurrency,
                    primaryAmountRenderer = primaryRenderer,
                    secondaryAmountRenderer = secondaryRenderer,
                    round = chartData.round
                )
            }
        }
    }
}
