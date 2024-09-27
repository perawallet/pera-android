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

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.BaseAccountAndAssetListItemMapper
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.module.account.core.ui.accountsorting.domain.util.ItemConfigurationHelper
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.module.account.sorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import javax.inject.Inject

internal class GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getAccountsDetail: GetAccountsDetail,
    private val baseAccountAndAssetListItemMapper: BaseAccountAndAssetListItemMapper,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount
) : GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction {

    override suspend operator fun invoke(
        accountFilterAssetId: Long?,
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        val accountsDetails = getAccountsDetail()
        return getSortedLocalAccounts().mapNotNull { account ->
            val accountDetail = accountsDetails.find { it.address == account.address } ?: return@mapNotNull null
            val isAccountTypeValid = isAccountTypeValid(excludedAccountTypes, accountDetail.accountType)
            if (isAccountTypeValid && accountDetail.accountType?.canSignTransaction() == true) {
                val isAssetIdValid = isAssetIdValid(accountDetail, accountFilterAssetId)
                if (isAssetIdValid) {
                    getAccountListItem(accountDetail, onLoadedAccountConfiguration, onFailedAccountConfiguration)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private suspend fun getAccountListItem(
        accountDetail: AccountDetail,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): BaseAccountAndAssetListItem.AccountListItem? {
        val accountItemConfiguration = ItemConfigurationHelper.configureListItem(
            accountDetail = accountDetail,
            accountAddress = accountDetail.address,
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

    private suspend fun isAssetIdValid(accountDetail: AccountDetail?, filteredAssetId: Long?): Boolean {
        if (filteredAssetId == null || filteredAssetId == ALGO_ASSET_ID) return true
        if (accountDetail?.address == null) return false
        return isAssetOwnedByAccount(accountDetail.address, filteredAssetId)
    }
}
