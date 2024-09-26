package com.algorand.android.module.account.local.domain.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount

fun interface AddLedgerBleAccount {
    suspend operator fun invoke(account: LocalAccount.LedgerBle)
}
