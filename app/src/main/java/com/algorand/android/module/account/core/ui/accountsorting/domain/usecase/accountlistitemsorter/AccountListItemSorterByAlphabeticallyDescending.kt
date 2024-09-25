package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import java.math.BigDecimal
import javax.inject.Inject

internal class AccountListItemSorterByAlphabeticallyDescending @Inject constructor() : AccountListItemSorter {

    override fun sortBaseAccountAndAssetListItem(
        currentList: List<BaseAccountAndAssetListItem.AccountListItem>
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        return currentList.sortedByDescending { it.alphabeticSortingField?.lowercase() }
    }

    override fun sortAccountAndAssetListItem(
        currentList: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem> {
        return currentList.sortedByDescending { it.alphabeticSortingField?.lowercase() }
    }

    override fun sortMap(
        accountNameAndValueMap: Map<AccountDisplayName, BigDecimal>
    ): Map<AccountDisplayName, BigDecimal> {
        val comparator = compareBy<AccountDisplayName> { accountDisplayName -> accountDisplayName.primaryDisplayName }
        return accountNameAndValueMap.toSortedMap(comparator)
    }
}
