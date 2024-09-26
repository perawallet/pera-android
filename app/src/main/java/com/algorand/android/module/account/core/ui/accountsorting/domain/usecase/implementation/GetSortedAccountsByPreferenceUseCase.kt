package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation

import com.algorand.android.module.account.core.ui.accountsorting.domain.mapper.AccountAndAssetAccountListItemMapper
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.module.account.core.ui.accountsorting.domain.util.ItemConfigurationHelper
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.module.account.sorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
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
                val accountItemConfiguration = com.algorand.android.module.account.core.ui.accountsorting.domain.util.ItemConfigurationHelper.configureListItem(
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
