package com.algorand.android.module.account.info.domain.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation

fun interface FetchAndCacheAccountInformation {
    suspend operator fun invoke(addresses: List<String>): Map<String, AccountInformation?>
}
