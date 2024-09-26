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

package com.algorand.android.modules.sorting.utils

import com.algorand.android.module.account.sorting.domain.model.AccountSortingTypeIdentifier
import javax.inject.Inject

class SortingTypeCreator @Inject constructor() {

    fun createForAccountSorting(): List<AccountSortingTypeIdentifier> {
        return mutableListOf<AccountSortingTypeIdentifier>().apply {
            add(AccountSortingTypeIdentifier.ALPHABETICALLY_ASCENDING)
            add(AccountSortingTypeIdentifier.ALPHABETICALLY_DESCENDING)
            add(AccountSortingTypeIdentifier.NUMERIC_ASCENDING)
            add(AccountSortingTypeIdentifier.NUMERIC_DESCENDING)
            add(AccountSortingTypeIdentifier.MANUAL)
        }
    }
}
