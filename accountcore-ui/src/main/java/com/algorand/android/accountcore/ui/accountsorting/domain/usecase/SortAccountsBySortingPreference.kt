package com.algorand.android.accountcore.ui.accountsorting.domain.usecase

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.accountcore.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier

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
