package com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase

internal interface IsAccountCacheUpdateRequired {
    suspend operator fun invoke(): Result<Boolean>
}
