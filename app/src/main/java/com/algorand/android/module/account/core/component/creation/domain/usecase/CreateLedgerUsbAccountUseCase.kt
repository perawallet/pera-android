package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.AddLedgerUsbAccount
import com.algorand.android.module.account.local.domain.usecase.CreateLedgerUsbAccount
import javax.inject.Inject

internal class CreateLedgerUsbAccountUseCase @Inject constructor(
    private val addLedgerUsbAccount: AddLedgerUsbAccount,
    private val cacheAccountDetail: CacheAccountDetail
) : CreateLedgerUsbAccount {

    override suspend fun invoke(address: String, deviceId: Int, indexInLedger: Int) {
        val ledgerUsbAccount = LocalAccount.LedgerUsb(address, deviceId, indexInLedger)
        addLedgerUsbAccount(ledgerUsbAccount)
        cacheAccountDetail(address)
    }
}
