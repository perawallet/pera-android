package com.algorand.android.module.account.sorting.domain.usecase

fun interface SetAccountOrderIndex {
    suspend operator fun invoke(address: String, orderIndex: Int)
}
