package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.repository.*
import com.algorand.android.module.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class GetAllLocalAccountAddressesAsFlowUseCase @Inject constructor(
    private val algo25AccountRepository: Algo25AccountRepository,
    private val ledgerBleAccountRepository: LedgerBleAccountRepository,
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository,
    private val noAuthAccountRepository: NoAuthAccountRepository
) : GetAllLocalAccountAddressesAsFlow {

    override fun invoke(): Flow<List<String>> {
        return combine(
            algo25AccountRepository.getAllAsFlow(),
            ledgerBleAccountRepository.getAllAsFlow(),
            ledgerUsbAccountRepository.getAllAsFlow(),
            noAuthAccountRepository.getAllAsFlow()
        ) { algo25Accounts, ledgerBleAccounts, ledgerUsbAccounts, noAuthAccounts ->
            buildList {
                addAll(algo25Accounts.map { it.address })
                addAll(ledgerBleAccounts.map { it.address })
                addAll(ledgerUsbAccounts.map { it.address })
                addAll(noAuthAccounts.map { it.address })
            }
        }
    }
}
