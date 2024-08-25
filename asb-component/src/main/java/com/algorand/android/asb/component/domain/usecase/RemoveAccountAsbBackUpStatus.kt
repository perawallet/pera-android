package com.algorand.android.asb.component.domain.usecase

fun interface RemoveAccountAsbBackUpStatus {
    suspend operator fun invoke(address: String)
}
