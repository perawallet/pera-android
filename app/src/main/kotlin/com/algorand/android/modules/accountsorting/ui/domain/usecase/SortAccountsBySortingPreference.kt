/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountsorting.ui.domain.usecase

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.ui.domain.model.AccountAndAssetListItem
import com.algorand.android.modules.accountsorting.ui.domain.model.BaseAccountAndAssetListItem

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

    suspend fun sortAccountLites(accountLites: Map<String, AccountLite>): Map<String, AccountLite>
}
