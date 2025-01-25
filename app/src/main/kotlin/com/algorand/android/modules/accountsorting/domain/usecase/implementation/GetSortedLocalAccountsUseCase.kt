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

package com.algorand.android.modules.accountsorting.domain.usecase.implementation

import com.algorand.android.modules.accountsorting.domain.usecase.GetSortedLocalAccounts
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import javax.inject.Inject

internal class GetSortedLocalAccountsUseCase @Inject constructor(
    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes
) : GetSortedLocalAccounts {

    override suspend fun invoke(): List<AccountOrderIndex> {
        return getAllAccountOrderIndexes()
            .sortedBy { it.index }
            .partition { it.index != NOT_INITIALIZED_ACCOUNT_INDEX }
            .let { (initializedAccounts, notInitializedAccounts) ->
                initializedAccounts + notInitializedAccounts
            }
    }

    companion object {
        private const val NOT_INITIALIZED_ACCOUNT_INDEX = -1
    }
}
