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

import com.algorand.android.modules.accountsorting.ui.domain.model.AccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorter
import com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter.AccountListItemSorterByManually
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier.ALPHABETICALLY_ASCENDING
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier.ALPHABETICALLY_DESCENDING
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier.MANUAL
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier.NUMERIC_ASCENDING
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier.NUMERIC_DESCENDING
import com.algorand.android.modules.accountsorting.domain.usecase.GetAccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.ui.domain.usecase.SortAccountsBySortingPreference
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
