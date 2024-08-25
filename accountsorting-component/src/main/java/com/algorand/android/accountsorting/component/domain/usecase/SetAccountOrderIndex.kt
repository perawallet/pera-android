package com.algorand.android.accountsorting.component.domain.usecase

fun interface SetAccountOrderIndex {
    suspend operator fun invoke(address: String, orderIndex: Int)
}
