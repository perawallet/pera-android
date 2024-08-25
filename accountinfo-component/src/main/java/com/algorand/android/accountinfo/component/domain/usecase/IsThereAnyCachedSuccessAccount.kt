package com.algorand.android.accountinfo.component.domain.usecase

interface IsThereAnyCachedSuccessAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
}
