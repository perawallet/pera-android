package com.algorand.android.modules.accounts.lite.domain.usecase

import com.algorand.android.modules.accounts.lite.domain.manager.AccountLiteManager
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import javax.inject.Inject

class GetAccountLiteUseCase @Inject constructor(
    private val accountLiteManager: AccountLiteManager
) : GetAccountLite {

    override suspend fun invoke(address: String): AccountLite? {
        return (accountLiteManager.localAccountLitesFlow.value as? AccountLiteCacheStatus.Data)
            ?.accountLites
            ?.get(address)
    }
}
