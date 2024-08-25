package com.algorand.android.accountsorting.component.domain.usecase

fun interface RemoveAccountOrderIndex {
    suspend operator fun invoke(address: String)
}
