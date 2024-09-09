package com.algorand.android.accountcore.ui.usecase

import com.algorand.android.accountcore.ui.model.AccountIconResource
import com.algorand.android.core.component.detail.domain.model.AccountType

interface GetAccountIconResourceByAccountType {
    operator fun invoke(accountType: AccountType?): AccountIconResource
    suspend operator fun invoke(address: String): AccountIconResource
}
