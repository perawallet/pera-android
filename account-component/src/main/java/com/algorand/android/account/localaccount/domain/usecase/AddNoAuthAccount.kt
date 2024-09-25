package com.algorand.android.account.localaccount.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount

fun interface AddNoAuthAccount {
    suspend operator fun invoke(account: LocalAccount.NoAuth)
}