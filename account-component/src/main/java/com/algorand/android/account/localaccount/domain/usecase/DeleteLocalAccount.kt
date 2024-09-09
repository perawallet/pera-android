package com.algorand.android.account.localaccount.domain.usecase

interface DeleteLocalAccount {
    suspend operator fun invoke(address: String)
}
