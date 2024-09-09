package com.algorand.android.accountinfo.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import kotlinx.coroutines.flow.Flow

fun interface GetAccountInformationFlow {
    operator fun invoke(address: String): Flow<AccountInformation?>
}
