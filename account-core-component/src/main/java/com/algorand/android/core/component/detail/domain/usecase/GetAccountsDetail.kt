package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.core.component.detail.domain.model.AccountDetail

interface GetAccountsDetail {
    suspend operator fun invoke(): List<AccountDetail>
}
