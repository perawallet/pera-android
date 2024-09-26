package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail

interface GetAccountIconDrawablePreview {
    suspend operator fun invoke(address: String): AccountIconDrawablePreview
    suspend operator fun invoke(accountDetail: AccountDetail): AccountIconDrawablePreview
}
