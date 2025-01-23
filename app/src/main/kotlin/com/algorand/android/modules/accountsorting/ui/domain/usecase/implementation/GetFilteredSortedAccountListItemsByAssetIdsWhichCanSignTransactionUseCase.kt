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
import com.algorand.android.modules.accountsorting.ui.domain.usecase.GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction
import com.algorand.android.modules.accountsorting.ui.domain.util.ItemConfigurationHelper
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.wallet.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

internal class GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransactionUseCase @Inject constructor(
    private val getSortedLocalAccounts: GetSortedLocalAccounts,
    private val getAccountsDetails: GetAccountsDetails,
    private val baseAccountAndAssetListItemMapper: BaseAccountAndAssetListItemMapper,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount
) : GetFilteredSortedAccountListItemsByAssetIdsWhichCanSignTransaction {

    override suspend operator fun invoke(
        accountFilterAssetId: Long?,
        excludedAccountTypes: List<AccountType>?,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        val accountsDetails = getAccountsDetails()
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
        if (filteredAssetId == null || filteredAssetId == ALGO_ID) return true
        if (accountDetail?.address == null) return false
        return isAssetOwnedByAccount(accountDetail.address, filteredAssetId)
    }
}
