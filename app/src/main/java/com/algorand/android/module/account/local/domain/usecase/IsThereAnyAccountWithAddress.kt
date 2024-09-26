package com.algorand.android.module.account.local.domain.usecase

interface IsThereAnyAccountWithAddress {
    suspend operator fun invoke(address: String): Boolean
}
