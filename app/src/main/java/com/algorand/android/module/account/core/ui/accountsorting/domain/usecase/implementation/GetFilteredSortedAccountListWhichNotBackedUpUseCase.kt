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

package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation

import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListWhichNotBackedUp
import com.algorand.android.module.account.core.ui.accountsorting.domain.util.ItemConfigurationHelper.configureListItem
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.sorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.module.asb.domain.usecase.GetBackedUpAccounts
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import javax.inject.Inject

internal class GetFilteredSortedAccountListWhichNotBackedUpUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getBackedUpAccounts: GetBackedUpAccounts,
    private val getAccountsDetail: GetAccountsDetail,
    private val baseAccountAndAssetListItemMapper: BaseAccountAndAssetListItemMapper,
) : GetFilteredSortedAccountListWhichNotBackedUp {

    override suspend fun invoke(
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        val backedUpAccounts = getBackedUpAccounts()
        val accountsDetail = getAccountsDetail()
        return getSortedLocalAccounts().mapIndexedNotNull { _, account ->
            val accountDetail = accountsDetail.find { it.address == account.address } ?: return@mapIndexedNotNull null
            val isBackedUp = backedUpAccounts.contains(account.address)
            val canSignTransaction = accountDetail.accountType?.canSignTransaction() == true
            if (canSignTransaction && !isBackedUp) {
                val accountItemConfiguration = configureListItem(
                    accountDetail = accountDetail,
                    accountAddress = account.address,
                    onLoadedAccountConfiguration = onLoadedAccountConfiguration,
                    onFailedAccountConfiguration = onFailedAccountConfiguration
                ) ?: return@mapIndexedNotNull null
                baseAccountAndAssetListItemMapper(accountItemConfiguration)
            } else {
                null
            }
        }
    }
}
