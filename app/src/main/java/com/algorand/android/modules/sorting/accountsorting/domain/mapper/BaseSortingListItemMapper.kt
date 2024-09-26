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

package com.algorand.android.modules.sorting.accountsorting.domain.mapper

import androidx.annotation.StringRes
import com.algorand.android.R
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.modules.sorting.accountsorting.domain.model.BaseAccountSortingListItem
import javax.inject.Inject

class BaseSortingListItemMapper @Inject constructor() {

    fun mapToSortingTypeListItem(
        accountSortingTypeIdentifier: AccountSortingTypeIdentifier,
        isChecked: Boolean
    ): BaseAccountSortingListItem.SortTypeListItem {
        return BaseAccountSortingListItem.SortTypeListItem(
            accountSortingTypeIdentifier,
            isChecked,
            getSortingTypeTextResId(accountSortingTypeIdentifier)
        )
    }

    fun mapToHeaderListItem(@StringRes headerTextRes: Int): BaseAccountSortingListItem.HeaderListItem {
        return BaseAccountSortingListItem.HeaderListItem(headerTextRes)
    }

    fun mapToAccountSortItem(
        accountListItem: AccountAndAssetListItem.AccountListItem
    ): BaseAccountSortingListItem.AccountSortListItem {
        return BaseAccountSortingListItem.AccountSortListItem(accountListItem)
    }

    private fun getSortingTypeTextResId(typeIdentifier: AccountSortingTypeIdentifier): Int {
        return when (typeIdentifier) {
            AccountSortingTypeIdentifier.MANUAL -> R.string.manually
            AccountSortingTypeIdentifier.ALPHABETICALLY_ASCENDING -> R.string.alphabetically_a_to_z
            AccountSortingTypeIdentifier.ALPHABETICALLY_DESCENDING -> R.string.alphabetically_z_to_a
            AccountSortingTypeIdentifier.NUMERIC_ASCENDING -> R.string.lowest_value_to_highest
            AccountSortingTypeIdentifier.NUMERIC_DESCENDING -> R.string.highest_value_to_lowest
        }
    }
}
