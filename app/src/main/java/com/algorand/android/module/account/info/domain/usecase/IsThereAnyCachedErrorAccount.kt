package com.algorand.android.module.account.info.domain.usecase

interface IsThereAnyCachedErrorAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
}
