package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.ui.model.AccountIconResource

interface GetAccountIconResourceByAccountType {
    operator fun invoke(accountType: AccountType?): AccountIconResource
    suspend operator fun invoke(address: String): AccountIconResource
}
