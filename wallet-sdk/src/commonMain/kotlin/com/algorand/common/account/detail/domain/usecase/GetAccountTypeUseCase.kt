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

import com.algorand.common.account.detail.domain.model.AccountType
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.usecase.GetAccountInformation
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.usecase.GetLocalAccounts

internal class GetAccountTypeUseCase(
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountInformation: GetAccountInformation
) : GetAccountType {

    override suspend fun invoke(address: String): AccountType? {
        val localAccounts = getLocalAccounts()
        val cachedAccount = getAccountInformation(address) ?: return null
        val account = localAccounts.firstOrNull { it.address == address } ?: return null
        return if (cachedAccount.rekeyAdminAddress != null) {
            getAccountTypeForRekeyedAccount(account, localAccounts, cachedAccount)
        } else {
            getAccountTypeForNonRekeyedAccount(account)
        }
    }

    private fun getAccountTypeForRekeyedAccount(
        account: LocalAccount,
        localAccounts: List<LocalAccount>,
        cachedAccount: AccountInformation
    ): AccountType {
        val doWeHaveSigner = localAccounts.any { it.address == cachedAccount.rekeyAdminAddress }
        return when {
            doWeHaveSigner -> AccountType.RekeyedAuth
            account is LocalAccount.NoAuth -> AccountType.NoAuth
            else -> AccountType.Rekeyed
        }
    }

    private fun getAccountTypeForNonRekeyedAccount(account: LocalAccount): AccountType {
        return when (account) {
            is LocalAccount.Algo25 -> AccountType.Algo25
            is LocalAccount.LedgerBle -> AccountType.LedgerBle
            is LocalAccount.NoAuth -> AccountType.NoAuth
            is LocalAccount.Bip39 -> AccountType.Bip39
        }
    }
}
