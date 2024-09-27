/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountselection.ui.usecase

import com.algorand.android.module.account.core.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.module.account.core.ui.accountselection.usecase.CreateLoadedAccountConfiguration
import com.algorand.android.module.account.core.ui.accountselection.usecase.CreateNotLoadedAccountConfiguration
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.modules.accountselection.ui.mapper.AddAssetAccountSelectionPreviewMapper
import com.algorand.android.modules.accountselection.ui.model.AddAssetAccountSelectionPreview
import javax.inject.Inject

class AddAssetAccountSelectionPreviewUseCase @Inject constructor(
    private val addAssetAccountSelectionPreviewMapper: AddAssetAccountSelectionPreviewMapper,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getAuthAccountItemsByAssetIds: GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction,
    private val createLoadedAccountConfiguration: CreateLoadedAccountConfiguration,
    private val createNotLoadedAccountConfiguration: CreateNotLoadedAccountConfiguration,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) {

    fun getInitialStatePreview() = addAssetAccountSelectionPreviewMapper.mapToAddAssetSelectionPreview(emptyList())

    suspend fun getAddAssetAccountSelectionPreview(): AddAssetAccountSelectionPreview {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol().orEmpty()
        val sortedAccountListItems = getAuthAccountItemsByAssetIds(
            accountFilterAssetId = null,
            excludedAccountTypes = null,
            onLoadedAccountConfiguration = {
                createLoadedAccountConfiguration(
                    accountDetail = this,
                    showHoldings = true,
                    selectedCurrencySymbol = selectedCurrencySymbol
                )
            },
            onFailedAccountConfiguration = {
                createNotLoadedAccountConfiguration(this)
            }
        )
        val accountSelectionListItems = sortedAccountListItems.map { accountListItem ->
            if (accountListItem.itemConfiguration.showWarning == true) {
                accountSelectionListItemMapper.mapToErrorAccountItem(accountListItem)
            } else {
                accountSelectionListItemMapper.mapToAccountItem(accountListItem)
            }
        }
        return addAssetAccountSelectionPreviewMapper.mapToAddAssetSelectionPreview(accountSelectionListItems)
    }
}
