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

package com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation

import com.algorand.android.modules.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.accountsorting.ui.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.modules.accountsorting.ui.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetFilteredSortedAccountListWhichNotBackedUp
import com.algorand.android.modules.accountsorting.ui.domain.util.ItemConfigurationHelper.configureListItem
import com.algorand.wallet.account.custom.domain.usecase.GetBackedUpAccounts
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import javax.inject.Inject

internal class GetFilteredSortedAccountListWhichNotBackedUpUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getBackedUpAccounts: GetBackedUpAccounts,
    private val getAccountsDetails: GetAccountsDetails,
    private val baseAccountAndAssetListItemMapper: BaseAccountAndAssetListItemMapper,
) : GetFilteredSortedAccountListWhichNotBackedUp {

    override suspend fun invoke(
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        val backedUpAccounts = getBackedUpAccounts()
        val accountsDetail = getAccountsDetails()
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
