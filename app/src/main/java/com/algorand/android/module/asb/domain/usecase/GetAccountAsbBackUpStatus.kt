package com.algorand.android.module.asb.domain.usecase

fun interface GetAccountAsbBackUpStatus {
    suspend operator fun invoke(accountAddress: String): Boolean
}
