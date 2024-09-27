package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.module.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

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
