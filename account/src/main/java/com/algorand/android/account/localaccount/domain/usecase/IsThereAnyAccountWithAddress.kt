package com.algorand.android.account.localaccount.domain.usecase

interface IsThereAnyAccountWithAddress {
    suspend operator fun invoke(address: String): Boolean
}
