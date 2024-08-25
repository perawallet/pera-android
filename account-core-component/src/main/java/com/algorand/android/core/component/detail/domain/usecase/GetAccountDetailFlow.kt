package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.core.component.detail.domain.model.AccountDetail
import kotlinx.coroutines.flow.Flow

interface GetAccountDetailFlow {
    operator fun invoke(address: String): Flow<AccountDetail?>
}
