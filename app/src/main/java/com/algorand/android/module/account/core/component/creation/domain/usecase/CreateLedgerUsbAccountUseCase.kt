package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.AddLedgerUsbAccount
import com.algorand.android.account.localaccount.domain.usecase.CreateLedgerUsbAccount
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
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
