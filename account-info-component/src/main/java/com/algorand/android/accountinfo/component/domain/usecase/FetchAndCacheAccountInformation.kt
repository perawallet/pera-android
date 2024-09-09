package com.algorand.android.accountinfo.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation

fun interface FetchAndCacheAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, AccountInformation?>
}
