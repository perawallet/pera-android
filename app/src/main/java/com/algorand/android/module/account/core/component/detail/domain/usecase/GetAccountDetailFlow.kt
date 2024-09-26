package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import kotlinx.coroutines.flow.Flow

interface GetAccountDetailFlow {
    operator fun invoke(address: String): Flow<AccountDetail?>
}
