package com.algorand.android.module.account.info.domain.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation

fun interface GetAllAccountInformation {
    suspend operator fun invoke(): Map<String, AccountInformation?>
}
