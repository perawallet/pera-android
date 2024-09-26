package com.algorand.android.module.account.sorting.domain.usecase

fun interface RemoveAccountOrderIndex {
    suspend operator fun invoke(address: String)
}
