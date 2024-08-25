package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.core.component.detail.domain.model.AccountType

interface GetAccountType {
    suspend operator fun invoke(address: String): AccountType?
}
