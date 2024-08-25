package com.algorand.android.account.localaccount.domain.usecase

interface CreateNoAuthAccount {
    suspend operator fun invoke(address: String)
}
