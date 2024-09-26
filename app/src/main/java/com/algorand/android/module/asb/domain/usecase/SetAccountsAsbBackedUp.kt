package com.algorand.android.module.asb.domain.usecase

fun interface SetAccountsAsbBackedUp {
    suspend operator fun invoke(accountAddresses: Set<String>)
}
