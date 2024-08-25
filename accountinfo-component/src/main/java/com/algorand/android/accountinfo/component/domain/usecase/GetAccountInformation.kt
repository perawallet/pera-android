package com.algorand.android.accountinfo.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation

fun interface GetAccountInformation {
    suspend operator fun invoke(address: String): AccountInformation?
}
