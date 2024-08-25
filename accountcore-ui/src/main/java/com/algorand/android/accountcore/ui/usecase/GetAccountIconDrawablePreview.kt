package com.algorand.android.accountcore.ui.usecase

import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.core.component.detail.domain.model.AccountDetail

interface GetAccountIconDrawablePreview {
    suspend operator fun invoke(address: String): AccountIconDrawablePreview
    suspend operator fun invoke(accountDetail: AccountDetail): AccountIconDrawablePreview
}