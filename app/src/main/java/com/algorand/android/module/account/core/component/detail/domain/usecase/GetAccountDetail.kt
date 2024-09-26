package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail

interface GetAccountDetail {
    suspend operator fun invoke(address: String): AccountDetail
}
