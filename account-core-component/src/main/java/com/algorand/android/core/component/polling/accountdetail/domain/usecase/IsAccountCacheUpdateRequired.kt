package com.algorand.android.core.component.polling.accountdetail.domain.usecase

internal interface IsAccountCacheUpdateRequired {
    suspend operator fun invoke(): Result<Boolean>
}
