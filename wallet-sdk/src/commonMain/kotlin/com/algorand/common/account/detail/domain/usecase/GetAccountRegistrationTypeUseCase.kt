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

package com.algorand.common.account.detail.domain.usecase

import com.algorand.common.account.detail.domain.model.AccountRegistrationType
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.usecase.GetLocalAccounts

internal class GetAccountRegistrationTypeUseCase(
    private val getLocalAccounts: GetLocalAccounts
) : GetAccountRegistrationType {

    override suspend fun invoke(address: String): AccountRegistrationType? {
        return when (getLocalAccounts().firstOrNull { it.address == address }) {
            is LocalAccount.Algo25 -> AccountRegistrationType.Algo25
            is LocalAccount.LedgerBle -> AccountRegistrationType.LedgerBle
            is LocalAccount.NoAuth -> AccountRegistrationType.NoAuth
            is LocalAccount.Bip39 -> AccountRegistrationType.Bip39
            else -> null
        }
    }
}
