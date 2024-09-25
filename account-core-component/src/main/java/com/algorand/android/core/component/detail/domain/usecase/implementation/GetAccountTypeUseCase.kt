package com.algorand.android.core.component.detail.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountType
import javax.inject.Inject

internal class GetAccountTypeUseCase @Inject constructor(
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
            account is LocalAccount.LedgerUsb -> TODO("Not yet implemented")
            else -> AccountType.Rekeyed
        }
    }

    private fun getAccountTypeForNonRekeyedAccount(account: LocalAccount): AccountType {
        return when (account) {
            is LocalAccount.Algo25 -> AccountType.Algo25
            is LocalAccount.LedgerBle -> AccountType.LedgerBle
            is LocalAccount.NoAuth -> AccountType.NoAuth
            is LocalAccount.LedgerUsb -> TODO("Not yet implemented")
        }
    }
}