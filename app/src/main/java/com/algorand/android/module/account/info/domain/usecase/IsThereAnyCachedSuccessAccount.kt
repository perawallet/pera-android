package com.algorand.android.module.account.info.domain.usecase

interface IsThereAnyCachedSuccessAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
}
