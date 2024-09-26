package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier

interface SortAccountsBySortingPreference {

    suspend fun sortBaseAccountAndAssetListItem(
        list: List<BaseAccountAndAssetListItem.AccountListItem>
    ): List<BaseAccountAndAssetListItem.AccountListItem>

    suspend fun sortAccountAndAssetListItem(
        list: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem>

    suspend fun sortAccountAndAssetListItem(
        sortingIdentifier: AccountSortingTypeIdentifier,
        list: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem>
}
