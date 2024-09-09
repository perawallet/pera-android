package com.algorand.android.accountcore.ui.accountsorting.domain.usecase.accountlistitemsorter

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.accountcore.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import java.math.BigDecimal
import javax.inject.Inject

internal class AccountListItemSorterByAlphabeticallyAscending @Inject constructor() : AccountListItemSorter {

    override fun sortBaseAccountAndAssetListItem(
        currentList: List<BaseAccountAndAssetListItem.AccountListItem>
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        return currentList.sortedBy { it.alphabeticSortingField?.lowercase() }
    }

    override fun sortAccountAndAssetListItem(
        currentList: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem> {
        return currentList.sortedBy { it.alphabeticSortingField?.lowercase() }
    }

    override fun sortMap(
        accountNameAndValueMap: Map<AccountDisplayName, BigDecimal>
    ): Map<AccountDisplayName, BigDecimal> {
        val comparator = compareBy<AccountDisplayName> { accountDisplayName -> accountDisplayName.primaryDisplayName }
        return accountNameAndValueMap.toSortedMap(comparator)
    }
}
