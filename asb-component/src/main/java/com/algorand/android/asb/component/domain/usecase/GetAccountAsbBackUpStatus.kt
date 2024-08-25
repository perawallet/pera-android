package com.algorand.android.asb.component.domain.usecase

fun interface GetAccountAsbBackUpStatus {
    suspend operator fun invoke(accountAddress: String): Boolean
}
