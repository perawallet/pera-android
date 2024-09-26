package com.algorand.android.module.account.local.domain.usecase

interface CreateLedgerBleAccount {

    suspend operator fun invoke(
        address: String,
        deviceMacAddress: String,
        indexInLedger: Int
    )
}
