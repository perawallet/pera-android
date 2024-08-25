package com.algorand.android.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.core.component.caching.domain.usecase.CacheAccountDetail
import javax.inject.Inject

internal class CreateLedgerBleAccountUseCase @Inject constructor(
    private val addLedgerBleAccount: AddLedgerBleAccount,
    private val cacheAccountDetail: CacheAccountDetail
) : CreateLedgerBleAccount {

    override suspend fun invoke(address: String, deviceMacAddress: String, indexInLedger: Int) {
        val ledgerBleAccount = LocalAccount.LedgerBle(address, deviceMacAddress, indexInLedger)
        addLedgerBleAccount(ledgerBleAccount)
        cacheAccountDetail(address)
    }
}
