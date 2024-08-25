package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.core.component.detail.domain.model.AccountDetail

interface GetAccountDetail {
    suspend operator fun invoke(address: String): AccountDetail
}
