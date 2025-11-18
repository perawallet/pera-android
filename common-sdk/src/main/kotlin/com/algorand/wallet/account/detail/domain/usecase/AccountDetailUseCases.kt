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

package com.algorand.wallet.account.detail.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountState
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.local.domain.model.LocalAccount

fun interface GetAccountState {
    suspend operator fun invoke(address: String): AccountState
}

interface GetAccountType {
    suspend operator fun invoke(address: String): AccountType?
    operator fun invoke(
        address: String,
        rekeyAdminAddress: String?,
        localAccounts: List<LocalAccount>
    ): AccountType?
}

interface GetAccountRegistrationType {
    suspend operator fun invoke(address: String): AccountRegistrationType?
    operator fun invoke(account: LocalAccount): AccountRegistrationType
}

fun interface GetAccountDetail {
    suspend operator fun invoke(address: String): AccountDetail
}

fun interface GetAccountsDetails {
    suspend operator fun invoke(): List<AccountDetail>
}

fun interface GetLocalRekeyedAccountCount {
    suspend operator fun invoke(authAddress: String): Int
}

fun interface IsAccountRekeyedToAnotherAccount {
    suspend operator fun invoke(address: String): Boolean
}
