package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.CreateLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
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
