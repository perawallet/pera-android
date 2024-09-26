package com.algorand.android.module.account.info.domain.usecase

import com.algorand.android.module.account.info.domain.model.AccountInformation
import kotlinx.coroutines.flow.Flow

fun interface GetAccountInformationFlow {
    operator fun invoke(address: String): Flow<AccountInformation?>
}
