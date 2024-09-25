package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.implementation

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.SortAccountsBySortingPreference
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorter
import com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter.AccountListItemSorterByManually
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier.ALPHABETICALLY_ASCENDING
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier.ALPHABETICALLY_DESCENDING
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier.MANUAL
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier.NUMERIC_ASCENDING
import com.algorand.android.accountsorting.component.domain.model.AccountSortingTypeIdentifier.NUMERIC_DESCENDING
import com.algorand.android.accountsorting.component.domain.usecase.GetAccountSortingTypeIdentifier
import javax.inject.Inject

internal class SortAccountsBySortingPreferenceUseCase @Inject constructor(
    private val getAccountSortingTypeIdentifier: GetAccountSortingTypeIdentifier,
    private val accountListItemSorterByAlphabeticallyAscending: AccountListItemSorter,
    private val accountListItemSorterByAlphabeticallyDescending: AccountListItemSorter,
    private val accountListItemSorterByNumericalAscending: AccountListItemSorter,
    private val accountListItemSorterByNumericalDescending: AccountListItemSorter,
    private val accountListItemSorterByManually: AccountListItemSorterByManually
) : SortAccountsBySortingPreference {

    override suspend fun sortBaseAccountAndAssetListItem(
        list: List<BaseAccountAndAssetListItem.AccountListItem>
    ): List<BaseAccountAndAssetListItem.AccountListItem> {
        return getAccountListItemSorter().sortBaseAccountAndAssetListItem(list)
    }

    override suspend fun sortAccountAndAssetListItem(
        list: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem> {
        return getAccountListItemSorter().sortAccountAndAssetListItem(list)
    }

    override suspend fun sortAccountAndAssetListItem(
        sortingIdentifier: AccountSortingTypeIdentifier,
        list: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem> {
        return getAccountListItemSorter(sortingIdentifier).sortAccountAndAssetListItem(list)
    }

    private suspend fun getAccountListItemSorter(): AccountListItemSorter {
        val identifier = getAccountSortingTypeIdentifier()
        return getAccountListItemSorter(identifier)
    }

    private fun getAccountListItemSorter(identifier: AccountSortingTypeIdentifier): AccountListItemSorter {
        return when (identifier) {
            ALPHABETICALLY_ASCENDING -> accountListItemSorterByAlphabeticallyAscending
            ALPHABETICALLY_DESCENDING -> accountListItemSorterByAlphabeticallyDescending
            NUMERIC_ASCENDING -> accountListItemSorterByNumericalAscending
            NUMERIC_DESCENDING -> accountListItemSorterByNumericalDescending
            MANUAL -> accountListItemSorterByManually
        }
    }
}
