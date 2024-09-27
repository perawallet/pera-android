package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.AddLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.CreateLedgerBleAccount
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
