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

package com.algorand.android.accountcore.ui.accountselection.usecase

import com.algorand.android.accountcore.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.accountcore.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.accountcore.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.accountcore.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import javax.inject.Inject

internal class GetAccountSelectionAccountItemsUseCase @Inject constructor(
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val createLoadedAccountConfiguration: CreateLoadedAccountConfiguration,
    private val createNotLoadedAccountConfiguration: CreateNotLoadedAccountConfiguration,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) : GetAccountSelectionAccountItems {

    override suspend fun invoke(
        showHoldings: Boolean,
        showFailedAccounts: Boolean
    ): List<BaseAccountSelectionListItem.BaseAccountItem> {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol().orEmpty()
        val sortedAccountListItems = getSortedAccountsByPreference(
            onLoadedAccountConfiguration = {
                createLoadedAccountConfiguration(
                    accountDetail = this,
                    showHoldings = showHoldings,
                    selectedCurrencySymbol = selectedCurrencySymbol
                )
            },
            onFailedAccountConfiguration = {
                createNotLoadedAccountConfiguration(this)
            }
        ).map {
            BaseAccountAndAssetListItem.AccountListItem(it.itemConfiguration)
        }
        return sortedAccountListItems.map { accountListItem ->
            if (accountListItem.itemConfiguration.showWarning == true) {
                accountSelectionListItemMapper.mapToErrorAccountItem(accountListItem)
            } else {
                accountSelectionListItemMapper.mapToAccountItem(accountListItem)
            }
        }.filter {
            if (showFailedAccounts) true else it !is BaseAccountSelectionListItem.BaseAccountItem.AccountErrorItem
        }
    }
}
