package com.algorand.android.module.account.local.domain.usecase

interface CreateNoAuthAccount {
    suspend operator fun invoke(address: String)
}
