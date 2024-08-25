package com.algorand.android.account.localaccount.domain.usecase

interface CreateLedgerUsbAccount {

    suspend operator fun invoke(
        address: String,
        deviceId: Int,
        indexInLedger: Int
    )
}