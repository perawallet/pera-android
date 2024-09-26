package com.algorand.android.module.account.info.domain.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation

fun interface GetAccountInformation {
    suspend operator fun invoke(address: String): AccountInformation?
}
