package com.algorand.android.module.asb.domain.usecase

fun interface SetAccountAsbBackUpStatus {
    suspend operator fun invoke(accountAddress: String, isBackedUp: Boolean)
}
