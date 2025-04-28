/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountsorting.ui.domain.usecase.accountlistitemsorter

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accountsorting.ui.domain.model.AccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.model.BaseAccountAndAssetListItem
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

    override fun sortAccountLites(accountLites: Map<String, AccountLite>): Map<String, AccountLite> {
        return accountLites.entries.sortedByDescending {
            it.value.customName.ifBlank { it.value.address }.lowercase()
        }.associate { it.toPair() }
    }
}
