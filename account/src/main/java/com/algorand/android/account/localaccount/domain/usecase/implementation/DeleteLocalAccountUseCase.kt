package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.repository.*
import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
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
