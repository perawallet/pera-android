package com.algorand.android.module.account.local.domain.usecase

interface CreateLedgerUsbAccount {

    suspend operator fun invoke(
        address: String,
        deviceId: Int,
        indexInLedger: Int
    )
}