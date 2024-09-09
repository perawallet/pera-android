package com.algorand.android.accountcore.ui.accountsorting.domain.usecase

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.model.AccountType

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
