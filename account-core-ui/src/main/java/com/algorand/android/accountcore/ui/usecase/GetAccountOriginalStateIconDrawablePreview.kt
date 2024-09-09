package com.algorand.android.accountcore.ui.usecase

import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview

interface GetAccountOriginalStateIconDrawablePreview {
    suspend operator fun invoke(address: String): AccountIconDrawablePreview
}
