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

package com.algorand.wallet.account.detail.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetAccountTypeUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) : GetAccountType {

    override suspend fun invoke(address: String): AccountType? {
        val localAccounts = getLocalAccounts()
        val rekeyAdminAddress = getAccountRekeyAdminAddress(address)
        val account = localAccounts.firstOrNull { it.algoAddress == address } ?: return null
        return if (rekeyAdminAddress != null) {
            getAccountTypeForRekeyedAccount(account, rekeyAdminAddress, localAccounts)
        } else {
            getAccountTypeForNonRekeyedAccount(account)
        }
    }

    override fun invoke(address: String, rekeyAdminAddress: String?, localAccounts: List<LocalAccount>): AccountType? {
        val account = localAccounts.firstOrNull { it.algoAddress == address } ?: return null
        return if (rekeyAdminAddress != null) {
            getAccountTypeForRekeyedAccount(account, rekeyAdminAddress, localAccounts)
        } else {
            getAccountTypeForNonRekeyedAccount(account)
        }
    }

    private fun getAccountTypeForRekeyedAccount(
        account: LocalAccount,
        rekeyAdminAddress: String?,
        localAccounts: List<LocalAccount>
    ): AccountType {
        val doWeHaveAuthSigner = localAccounts.any {
            it.algoAddress == rekeyAdminAddress && it !is LocalAccount.NoAuth
        }
        return when {
            doWeHaveAuthSigner -> AccountType.RekeyedAuth
            account is LocalAccount.NoAuth -> AccountType.NoAuth
            else -> AccountType.Rekeyed
        }
    }

    private fun getAccountTypeForNonRekeyedAccount(account: LocalAccount): AccountType {
        return when (account) {
            is LocalAccount.Algo25 -> AccountType.Algo25
            is LocalAccount.LedgerBle -> AccountType.LedgerBle
            is LocalAccount.NoAuth -> AccountType.NoAuth
            is LocalAccount.HdKey -> AccountType.HdKey
        }
    }
}
