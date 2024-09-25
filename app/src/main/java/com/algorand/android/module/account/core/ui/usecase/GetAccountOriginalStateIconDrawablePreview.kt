package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview

interface GetAccountOriginalStateIconDrawablePreview {
    suspend operator fun invoke(address: String): AccountIconDrawablePreview
}
