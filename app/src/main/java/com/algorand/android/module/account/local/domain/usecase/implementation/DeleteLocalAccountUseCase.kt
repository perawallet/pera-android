package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.android.module.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import javax.inject.Inject

internal class DeleteLocalAccountUseCase @Inject constructor(
    private val algo25AccountRepository: Algo25AccountRepository,
    private val noAuthAccountRepository: NoAuthAccountRepository,
    private val ledgerBleAccountRepository: LedgerBleAccountRepository
) : DeleteLocalAccount {

    override suspend fun invoke(address: String) {
        algo25AccountRepository.deleteAccount(address)
        noAuthAccountRepository.deleteAccount(address)
        ledgerBleAccountRepository.deleteAccount(address)
    }
}
