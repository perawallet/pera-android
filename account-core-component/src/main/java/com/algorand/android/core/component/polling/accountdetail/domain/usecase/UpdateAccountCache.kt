package com.algorand.android.core.component.polling.accountdetail.domain.usecase

internal interface UpdateAccountCache {
    suspend operator fun invoke()
}
