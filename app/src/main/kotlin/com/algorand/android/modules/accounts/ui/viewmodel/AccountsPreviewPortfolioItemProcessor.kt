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
import com.algorand.android.ui.accounts.usecase.GetFilteredPortfolioAccountLites
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.wallet.account.local.domain.model.LocalAccount
import java.math.BigDecimal
import javax.inject.Inject

class AccountsPreviewPortfolioItemProcessor @Inject constructor(
    private val isThereAnyCachedErrorAccount: IsThereAnyCachedErrorAccount,
    private val isThereAnyCachedSuccessAccount: IsThereAnyCachedSuccessAccount,
    private val portfolioValueItemMapper: PortfolioValueItemMapper,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer,
    private val getFilteredPortfolioAccountLites: GetFilteredPortfolioAccountLites
) {

    suspend fun getPortfolioItem(
        accountLites: Map<String, AccountLite>,
        amountRendererType: AmountRenderer.RenderType,
        localAccounts: List<LocalAccount>
    ): BasePortfolioValueItem {
        return if (!isThereAnyCachedErrorAccount(localAccounts, excludeNoAuthAccounts = true)) {
            getPortfolioValueSuccessItem(accountLites, amountRendererType)
        } else if (isThereAnyCachedSuccessAccount(excludeNoAuthAccounts = true)) {
            getPortfolioValuePartialErrorItem(accountLites, amountRendererType)
        } else {
            portfolioValueItemMapper.mapToPortfolioValuesErrorItem()
        }
    }

    private fun getPortfolioValueSuccessItem(
        accountLites: Map<String, AccountLite>,
        amountRendererType: AmountRenderer.RenderType
    ): SuccessPortfolioValueItem {
        val (totalPrimaryValue, totalSecondaryValue) = getTotalPrimaryAndSecondaryValues(accountLites)
        return portfolioValueItemMapper.mapToPortfolioValuesSuccessItem(
            primaryAmountRenderer = getCompactPrimaryAmountRenderer(totalPrimaryValue, amountRendererType),
            secondaryAmountRenderer = getCompactSecondaryAmountRenderer(totalSecondaryValue, amountRendererType)
        )
    }

    private fun getPortfolioValuePartialErrorItem(
        accountLites: Map<String, AccountLite>,
        amountRendererType: AmountRenderer.RenderType
    ): PartialErrorPortfolioValueItem {
        val (totalPrimaryValue, totalSecondaryValue) = getTotalPrimaryAndSecondaryValues(accountLites)
        return portfolioValueItemMapper.mapToPortfolioValuesPartialErrorItem(
            primaryAmountRenderer = getCompactPrimaryAmountRenderer(totalPrimaryValue, amountRendererType),
            secondaryAmountRenderer = getCompactSecondaryAmountRenderer(totalSecondaryValue, amountRendererType)
        )
    }

    private fun getTotalPrimaryAndSecondaryValues(accountLite: Map<String, AccountLite>): Pair<PeraAmount, PeraAmount> {
        var totalPrimaryValue = BigDecimal.ZERO
        var totalSecondaryValue = BigDecimal.ZERO

        getFilteredPortfolioAccountLites(accountLite).values.forEach { account ->
            totalPrimaryValue += account.cachedInfo?.primaryAccountValue ?: BigDecimal.ZERO
            totalSecondaryValue += account.cachedInfo?.secondaryAccountValue ?: BigDecimal.ZERO
        }

        return Pair(PeraAmount(totalPrimaryValue), PeraAmount(totalSecondaryValue))
    }
}
