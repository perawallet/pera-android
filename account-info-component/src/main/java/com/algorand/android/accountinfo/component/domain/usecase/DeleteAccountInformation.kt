package com.algorand.android.accountinfo.component.domain.usecase

fun interface DeleteAccountInformation {
    suspend operator fun invoke(address: String)
}
