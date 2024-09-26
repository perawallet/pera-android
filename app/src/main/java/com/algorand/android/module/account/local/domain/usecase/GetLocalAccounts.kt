package com.algorand.android.module.account.local.domain.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount

interface GetLocalAccounts {
    suspend operator fun invoke(): List<LocalAccount>
}
