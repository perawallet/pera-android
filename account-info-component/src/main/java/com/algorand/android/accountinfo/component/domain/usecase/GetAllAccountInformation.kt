package com.algorand.android.accountinfo.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation

fun interface GetAllAccountInformation {
    suspend operator fun invoke(): Map<String, AccountInformation?>
}
