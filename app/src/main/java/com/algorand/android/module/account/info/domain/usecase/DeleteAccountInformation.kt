package com.algorand.android.module.account.info.domain.usecase

fun interface DeleteAccountInformation {
    suspend operator fun invoke(address: String)
}
