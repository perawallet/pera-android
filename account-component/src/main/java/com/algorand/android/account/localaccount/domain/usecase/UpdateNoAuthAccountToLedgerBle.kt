package com.algorand.android.account.localaccount.domain.usecase

interface UpdateNoAuthAccountToLedgerBle {
    suspend operator fun invoke(address: String, deviceMacAddress: String, indexInLedger: Int)
}
