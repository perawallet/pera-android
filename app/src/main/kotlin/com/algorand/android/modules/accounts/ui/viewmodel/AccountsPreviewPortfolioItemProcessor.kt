/*
 * Copyright 2025 Pera Wallet, LDA
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
import com.algorand.android.modules.accounts.ui.model.PortfolioItemProcessorData
import com.algorand.android.utils.formatAsCurrency
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.wallet.account.local.domain.model.LocalAccount
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
        return with(data) {
            portfolioValueItemMapper.mapToPortfolioValuesSuccessItem(
                formattedPrimaryAccountValue = totalPrimaryValue.formatAsCurrency(primaryCurrencySymbol),
                formattedSecondaryAccountValue = totalSecondaryValue.formatAsCurrency(secondaryCurrencySymbol)
            )
        }
    }

    private fun getPortfolioValuePartialErrorItem(data: PortfolioItemProcessorData): PartialErrorPortfolioValueItem {
        return with(data) {
            portfolioValueItemMapper.mapToPortfolioValuesPartialErrorItem(
                formattedPrimaryAccountValue = totalPrimaryValue.formatAsCurrency(data.primaryCurrencySymbol),
                formattedSecondaryAccountValue = totalSecondaryValue.formatAsCurrency(data.secondaryCurrencySymbol)
            )
        }
    }
}
