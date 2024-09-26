package com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase

internal interface UpdateAccountCache {
    suspend operator fun invoke()
}
