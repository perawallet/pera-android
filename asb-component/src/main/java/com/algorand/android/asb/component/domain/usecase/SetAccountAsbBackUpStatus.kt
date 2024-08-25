package com.algorand.android.asb.component.domain.usecase

fun interface SetAccountAsbBackUpStatus {
    suspend operator fun invoke(accountAddress: String, isBackedUp: Boolean)
}
