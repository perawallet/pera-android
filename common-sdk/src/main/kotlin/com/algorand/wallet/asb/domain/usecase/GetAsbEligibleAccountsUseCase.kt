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

package com.algorand.wallet.asb.domain.usecase

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetAsbEligibleAccountsUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts
) : GetAsbEligibleAccounts {

    override suspend fun invoke(): List<LocalAccount> {
        return getLocalAccounts().filter { isAccountEligible(it) }
    }

    private fun isAccountEligible(account: LocalAccount): Boolean {
        return when (account) {
            is LocalAccount.Algo25 -> true
            is LocalAccount.HdKey -> false // TODO will be implemented when HD key is supported
            is LocalAccount.LedgerBle -> false
            is LocalAccount.NoAuth -> true
        }
    }
}
