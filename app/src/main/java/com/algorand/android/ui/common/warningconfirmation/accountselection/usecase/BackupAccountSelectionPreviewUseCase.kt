/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.common.warningconfirmation.accountselection.usecase

import com.algorand.android.module.account.core.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.module.account.core.ui.accountselection.usecase.CreateLoadedAccountConfiguration
import com.algorand.android.module.account.core.ui.accountselection.usecase.CreateNotLoadedAccountConfiguration
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListWhichNotBackedUp
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.ui.common.warningconfirmation.accountselection.mapper.BackupAccountSelectionPreviewMapper
import com.algorand.android.ui.common.warningconfirmation.accountselection.model.BackupAccountSelectionPreview
import javax.inject.Inject

class BackupAccountSelectionPreviewUseCase @Inject constructor(
    private val backupAccountSelectionPreviewMapper: BackupAccountSelectionPreviewMapper,
    private val getFilteredSortedAccountListWhichNotBackedUp: GetFilteredSortedAccountListWhichNotBackedUp,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val createLoadedAccountConfiguration: CreateLoadedAccountConfiguration,
    private val createNotLoadedAccountConfiguration: CreateNotLoadedAccountConfiguration,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) {

    fun getInitialStatePreview() = backupAccountSelectionPreviewMapper.mapToBackupAccountSelectionPreview(emptyList())

    suspend fun getBackupAccountSelectionPreview(): BackupAccountSelectionPreview {
        val selectedCurrencySymbol = getPrimaryCurrencySymbol().orEmpty()
        val sortedAccountListItems = getFilteredSortedAccountListWhichNotBackedUp(
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
        val accountListItems = sortedAccountListItems.map { accountListItem ->
            if (accountListItem.itemConfiguration.showWarning == true) {
                accountSelectionListItemMapper.mapToErrorAccountItem(accountListItem)
            } else {
                accountSelectionListItemMapper.mapToAccountItem(accountListItem)
            }
        }
        return backupAccountSelectionPreviewMapper.mapToBackupAccountSelectionPreview(accountListItems)
    }
}
