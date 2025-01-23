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
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.accountsorting.ui.domain.mapper.AccountAndAssetAccountListItemMapper
import com.algorand.android.modules.accountsorting.ui.domain.model.AccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.modules.accountsorting.ui.domain.util.ItemConfigurationHelper
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import javax.inject.Inject

internal class GetSortedAccountsByPreferenceUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getAccountDetail: GetAccountDetail,
    private val accountAndAssetAccountListItemMapper: AccountAndAssetAccountListItemMapper,
    private val sortAccountsBySortingPreference: SortAccountsBySortingPreference
) : GetSortedAccountsByPreference {

    override suspend fun invoke(
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<AccountAndAssetListItem.AccountListItem> {
        val accountListItems = getAccountListItems(
            excludedAccountTypes = excludedAccountTypes,
            onLoadedAccountConfiguration = onLoadedAccountConfiguration,
            onFailedAccountConfiguration = onFailedAccountConfiguration
        )
        return sortAccountsBySortingPreference.sortAccountAndAssetListItem(accountListItems)
    }

    override suspend fun invoke(
        sortingIdentifier: AccountSortingTypeIdentifier,
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<AccountAndAssetListItem.AccountListItem> {
        val accountListItems = getAccountListItems(
            excludedAccountTypes = excludedAccountTypes,
            onLoadedAccountConfiguration = onLoadedAccountConfiguration,
            onFailedAccountConfiguration = onFailedAccountConfiguration
        )
        return sortAccountsBySortingPreference.sortAccountAndAssetListItem(sortingIdentifier, accountListItems)
    }

    private suspend fun getAccountListItems(
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<AccountAndAssetListItem.AccountListItem> {
        val localAccounts = getSortedLocalAccounts()
        return localAccounts.mapIndexedNotNull { _, account ->
            val accountDetail = getAccountDetail(account.address)
            val isAccountTypeValid = isAccountTypeValid(excludedAccountTypes, accountDetail.accountType)
            if (isAccountTypeValid) {
                val accountItemConfiguration = ItemConfigurationHelper.configureListItem(
                    accountDetail = accountDetail,
                    accountAddress = account.address,
                    onLoadedAccountConfiguration = onLoadedAccountConfiguration,
                    onFailedAccountConfiguration = onFailedAccountConfiguration
                ) ?: return@mapIndexedNotNull null
                accountAndAssetAccountListItemMapper(accountItemConfiguration)
            } else {
                null
            }
        }
    }

    private fun isAccountTypeValid(excludedAccountTypes: List<AccountType>?, accountType: AccountType?): Boolean {
        // This means, there is no filter
        if (excludedAccountTypes.isNullOrEmpty()) return true
        return accountType !in excludedAccountTypes
    }
}
