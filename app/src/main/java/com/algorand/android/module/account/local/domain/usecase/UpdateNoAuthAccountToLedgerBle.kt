package com.algorand.android.module.account.local.domain.usecase

interface UpdateNoAuthAccountToLedgerBle {
    suspend operator fun invoke(address: String, deviceMacAddress: String, indexInLedger: Int)
}
