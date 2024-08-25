package com.algorand.android.asb.component.domain.usecase

fun interface SetAccountsAsbBackedUp {
    suspend operator fun invoke(accountAddresses: Set<String>)
}
