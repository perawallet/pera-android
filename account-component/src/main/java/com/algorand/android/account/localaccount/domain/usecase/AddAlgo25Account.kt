package com.algorand.android.account.localaccount.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount

fun interface AddAlgo25Account {
    suspend operator fun invoke(account: LocalAccount.Algo25)
}
