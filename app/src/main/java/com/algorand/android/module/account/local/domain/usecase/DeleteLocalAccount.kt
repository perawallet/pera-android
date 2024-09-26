package com.algorand.android.module.account.local.domain.usecase

interface DeleteLocalAccount {
    suspend operator fun invoke(address: String)
}
