package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.repository.*
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import kotlinx.coroutines.*
import javax.inject.Inject

internal class GetLocalAccountsUseCase @Inject constructor(
    private val algo25AccountRepository: Algo25AccountRepository,
    private val ledgerBleAccountRepository: LedgerBleAccountRepository,
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository,
    private val noAuthAccountRepository: NoAuthAccountRepository
) : GetLocalAccounts {

    override suspend fun invoke(): List<LocalAccount> {
        return withContext(Dispatchers.IO) {
            val deferredAlgo25Accounts = async { algo25AccountRepository.getAll() }
            val deferredLedgerBleAccounts = async { ledgerBleAccountRepository.getAll() }
            val deferredLedgerUsbAccounts = async { ledgerUsbAccountRepository.getAll() }
            val deferredNoAuthAccounts = async { noAuthAccountRepository.getAll() }
            awaitAll(
                deferredAlgo25Accounts,
                deferredLedgerBleAccounts,
                deferredLedgerUsbAccounts,
                deferredNoAuthAccounts
            ).flatten()
        }
    }
}
