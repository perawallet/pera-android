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

package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.modules.accounts.domain.mapper.PortfolioValueItemMapper
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem.PartialErrorPortfolioValueItem
import com.algorand.android.modules.accounts.domain.model.BasePortfolioValueItem.SuccessPortfolioValueItem
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.ui.model.PortfolioItemProcessorData
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.CompactFormattedAmount
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType.Asset
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType.Fiat
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.wallet.account.local.domain.model.LocalAccount
import java.math.BigDecimal
import javax.inject.Inject

class AccountsPreviewPortfolioItemProcessor @Inject constructor(
    private val isThereAnyCachedErrorAccount: IsThereAnyCachedErrorAccount,
    private val isThereAnyCachedSuccessAccount: IsThereAnyCachedSuccessAccount,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
) {

    suspend fun getPortfolioItem(
        data: PortfolioItemProcessorData,
        localAccounts: List<LocalAccount>
    ): BasePortfolioValueItem {
        return if (!isThereAnyCachedErrorAccount(localAccounts, excludeNoAuthAccounts = true)) {
            getPortfolioValueSuccessItem(data)
        } else if (isThereAnyCachedSuccessAccount(excludeNoAuthAccounts = true)) {
            getPortfolioValuePartialErrorItem(data)
        } else {
            portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
        }
    }

    private fun getPortfolioValueSuccessItem(data: PortfolioItemProcessorData): SuccessPortfolioValueItem {
        val (totalPrimaryValue, totalSecondaryValue) = getTotalPrimaryAndSecondaryValues(data.accountLites)
        return portfolioValueItemMapper.mapToPortfolioValuesSuccessItem(
            primaryAmountRenderer = getRenderer(data, totalPrimaryValue, data.primaryCurrencySymbol),
            secondaryAmountRenderer = getRenderer(data, totalSecondaryValue, data.secondaryCurrencySymbol)
        )
    }

    private fun getPortfolioValuePartialErrorItem(data: PortfolioItemProcessorData): PartialErrorPortfolioValueItem {
        val (totalPrimaryValue, totalSecondaryValue) = getTotalPrimaryAndSecondaryValues(data.accountLites)
        return portfolioValueItemMapper.mapToPortfolioValuesPartialErrorItem(
            primaryAmountRenderer = getRenderer(data, totalPrimaryValue, data.primaryCurrencySymbol),
            secondaryAmountRenderer = getRenderer(data, totalSecondaryValue, data.secondaryCurrencySymbol)
        )
    }

    private fun getRenderer(data: PortfolioItemProcessorData, amount: BigDecimal, symbol: String): AmountRenderer {
        val fractionalType = if (data.isPrimaryCurrencyAlgo) Asset else Fiat
        val formattedAmount = CompactFormattedAmount(PeraAmount(amount), fractionalType)
        return AmountRenderer(formattedAmount, data.amountRendererType, symbol)
    }

    private fun getTotalPrimaryAndSecondaryValues(accountLite: Map<String, AccountLite>): Pair<BigDecimal, BigDecimal> {
        var totalPrimaryValue = BigDecimal.ZERO
        var totalSecondaryValue = BigDecimal.ZERO

        accountLite.values.forEach { account ->
            totalPrimaryValue += account.cachedInfo?.primaryAccountValue ?: BigDecimal.ZERO
            totalSecondaryValue += account.cachedInfo?.secondaryAccountValue ?: BigDecimal.ZERO
        }

        return Pair(totalPrimaryValue, totalSecondaryValue)
    }
}
