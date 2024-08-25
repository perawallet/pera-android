package com.algorand.android.account.localaccount.domain.usecase

interface CreateLedgerBleAccount {

    suspend operator fun invoke(
        address: String,
        deviceMacAddress: String,
        indexInLedger: Int
    )
}
