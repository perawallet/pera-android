package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.info.domain.usecase.GetAllAccountInformation
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.asb.domain.usecase.GetBackedUpAccounts
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class GetNotBackedUpAccountsUseCase @Inject constructor(
    private val getAllAccountInformation: GetAllAccountInformation,
    private val getBackedUpAccounts: GetBackedUpAccounts,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val getAccountDetail: GetAccountDetail,
    private val getLocalAccounts: GetLocalAccounts
) : GetNotBackedUpAccounts {

    override suspend fun invoke(): List<String> {
        val accounts = mutableListOf<String>()
        val localAccounts = getLocalAccounts()
        val localAccountsCachedAccounts = getAllAccountInformation().values.filterNotNull().filter {
            localAccounts.any { localAccount -> localAccount.address == it.address }
        }
        val backedUpAccounts = getBackedUpAccounts()
        localAccountsCachedAccounts.forEach { accountInfo ->
            if (backedUpAccounts.contains(accountInfo.address)) return@forEach
            val isAuthAccount = localAccountsCachedAccounts.any { it.rekeyAdminAddress == accountInfo.address }
            val hasBalance = getAccountTotalValue(accountInfo.address, true).primaryAccountValue.compareTo(ZERO) == 1
            val isNoAuthAccount = getAccountDetail(accountInfo.address).accountType == AccountType.NoAuth
            if ((isAuthAccount || hasBalance) && !isNoAuthAccount) {
                accounts.add(accountInfo.address)
            }
        }
        return accounts
    }
}
