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

package com.algorand.android.modules.accountsorting.ui.domain.usecase.implementation

import com.algorand.android.modules.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.modules.accountsorting.ui.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.modules.accountsorting.ui.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.modules.accountsorting.ui.domain.util.ItemConfigurationHelper
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

internal class GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getAccountLiteCacheData: GetAccountLiteCacheData,
    private val baseAccountAndAssetListItemMapper: BaseAccountAndAssetListItemMapper,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount
) : GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction {

    override suspend operator fun invoke(
        accountFilterAssetId: Long?,
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountLite.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend AccountLite.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        val accountLites = getAccountLiteCacheData()?.accountLites.orEmpty()
        return getSortedLocalAccounts().mapNotNull { account ->
            val accountLite = accountLites[account.address] ?: return@mapNotNull null
            val isAccountTypeValid = isAccountTypeValid(excludedAccountTypes, accountLite.cachedInfo?.type)
            if (isAccountTypeValid && accountLite.cachedInfo?.type?.canSignTransaction() == true) {
                val isAssetIdValid = isAssetIdValid(accountLite.address, accountFilterAssetId)
                if (isAssetIdValid) {
                    getAccountListItem(accountLite, onLoadedAccountConfiguration, onFailedAccountConfiguration)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private suspend fun getAccountListItem(
        accountLite: AccountLite,
        onLoadedAccountConfiguration: suspend AccountLite.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend AccountLite.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): BaseAccountAndAssetListItem.AccountListItem? {
        val accountItemConfiguration = ItemConfigurationHelper.configureListItem(
            accountLite = accountLite,
            onLoadedAccountConfiguration = onLoadedAccountConfiguration,
            onFailedAccountConfiguration = onFailedAccountConfiguration
        ) ?: return null
        return baseAccountAndAssetListItemMapper(accountItemConfiguration)
    }

    private fun isAccountTypeValid(excludedAccountTypes: List<AccountType>?, accountType: AccountType?): Boolean {
        // This means, there is no filter
        if (excludedAccountTypes.isNullOrEmpty()) return true
        return accountType !in excludedAccountTypes
    }

    private suspend fun isAssetIdValid(address: String, filteredAssetId: Long?): Boolean {
        if (filteredAssetId == null || filteredAssetId == ALGO_ID) return true
        return isAssetOwnedByAccount(address, filteredAssetId)
    }
}
