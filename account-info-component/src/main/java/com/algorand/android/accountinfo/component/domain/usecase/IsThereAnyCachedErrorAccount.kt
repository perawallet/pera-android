package com.algorand.android.accountinfo.component.domain.usecase

interface IsThereAnyCachedErrorAccount {
    suspend operator fun invoke(excludeNoAuthAccounts: Boolean): Boolean
}
