package com.algorand.android.module.asb.domain.usecase

fun interface RemoveAccountAsbBackUpStatus {
    suspend operator fun invoke(address: String)
}
