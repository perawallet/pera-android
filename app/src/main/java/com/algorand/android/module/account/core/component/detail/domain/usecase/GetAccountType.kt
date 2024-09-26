package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType

interface GetAccountType {
    suspend operator fun invoke(address: String): AccountType?
}
