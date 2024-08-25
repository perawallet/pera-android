package com.algorand.android.account.localaccount.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount

interface GetLocalAccounts {
    suspend operator fun invoke(): List<LocalAccount>
}
