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

package com.algorand.android.modules.accountsorting.data.repository

import com.algorand.android.modules.accountsorting.domain.model.AccountSortingTypeIdentifier
import com.algorand.android.modules.accountsorting.domain.repository.AccountSortingRepository
import com.algorand.android.sharedpref.SharedPrefLocalSource
import javax.inject.Inject

internal class AccountSortingRepositoryImpl @Inject constructor(
    private val accountSortingTypeIdentifierLocalSource: SharedPrefLocalSource<String>
) : AccountSortingRepository {

    override suspend fun saveAccountSortPreference(identifier: AccountSortingTypeIdentifier) {
        accountSortingTypeIdentifierLocalSource.saveData(identifier.name)
    }

    override suspend fun getAccountSortPreference(): AccountSortingTypeIdentifier {
        val preference = accountSortingTypeIdentifierLocalSource.getDataOrNull()
        return AccountSortingTypeIdentifier.entries.firstOrNull {
            it.name == preference
        } ?: AccountSortingTypeIdentifier.DEFAULT_SORTING_TYPE
    }
}
