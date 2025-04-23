package com.algorand.android.modules.accounts.lite.domain.usecase

import com.algorand.android.modules.accounts.lite.domain.manager.AccountLiteManager
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import javax.inject.Inject

internal class GetAccountLiteCacheDataUseCase @Inject constructor(
    private val accountLiteManager: AccountLiteManager
) : GetAccountLiteCacheData {

    override fun invoke(): AccountLiteCacheStatus.Data? {
        return accountLiteManager.localAccountLitesFlow.value as? AccountLiteCacheStatus.Data
    }
}
