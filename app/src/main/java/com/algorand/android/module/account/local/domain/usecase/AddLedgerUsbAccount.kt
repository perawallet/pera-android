package com.algorand.android.module.account.local.domain.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount

fun interface AddLedgerUsbAccount {
    suspend operator fun invoke(account: LocalAccount.LedgerUsb)
}
