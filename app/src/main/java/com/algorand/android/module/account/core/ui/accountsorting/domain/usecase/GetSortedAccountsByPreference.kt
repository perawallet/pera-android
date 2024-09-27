package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier

interface GetSortedAccountsByPreference {

    suspend operator fun invoke(
        excludedAccountTypes: List<AccountType>? = null,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<AccountAndAssetListItem.AccountListItem>

    suspend operator fun invoke(
        sortingIdentifier: AccountSortingTypeIdentifier,
        excludedAccountTypes: List<AccountType>? = null,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): List<AccountAndAssetListItem.AccountListItem>
}
