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
import com.algorand.android.accountcore.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import javax.inject.Inject

internal class GetAccountSelectionAccountsWhichCanSignTransactionUseCase @Inject constructor(
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction: GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction,
    private val createLoadedAccountConfiguration: CreateLoadedAccountConfiguration,
    private val createNotLoadedAccountConfiguration: CreateNotLoadedAccountConfiguration,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) : GetAccountSelectionAccountsWhichCanSignTransaction {

    override suspend fun invoke(
        showHoldings: Boolean,
        showFailedAccounts: Boolean,
        assetId: Long?,
        excludedAccountTypes: List<AccountType>?
    ): List<BaseAccountSelectionListItem.BaseAccountItem> {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol().orEmpty()
        val sortedAccountListItems = getFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction(
            accountFilterAssetId = assetId,
            excludedAccountTypes = excludedAccountTypes,
            onLoadedAccountConfiguration = {
                createLoadedAccountConfiguration(this, showHoldings, selectedCurrencySymbol)
            },
            onFailedAccountConfiguration = {
                createNotLoadedAccountConfiguration(this)
            }
        )
        return sortedAccountListItems.map { accountListItem ->
            if (accountListItem.itemConfiguration.showWarning == true) {
                accountSelectionListItemMapper.mapToErrorAccountItem(accountListItem)
            } else {
                accountSelectionListItemMapper.mapToAccountItem(accountListItem)
            }
        }
    }
}
