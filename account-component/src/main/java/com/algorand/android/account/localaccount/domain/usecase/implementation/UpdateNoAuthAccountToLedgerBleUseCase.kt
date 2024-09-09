package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.*
import javax.inject.Inject

internal class UpdateNoAuthAccountToLedgerBleUseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val createLedgerBleAccount: CreateLedgerBleAccount
) : UpdateNoAuthAccountToLedgerBle {

    override suspend fun invoke(address: String, deviceMacAddress: String, indexInLedger: Int) {
        deleteLocalAccount(address)
        createLedgerBleAccount(address, deviceMacAddress, indexInLedger)
    }
}
