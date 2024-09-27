package com.algorand.android.module.account.core.component.polling.accountdetail.data.repository

import com.algorand.android.module.account.core.component.caching.data.service.MobileAlgorandApi
import com.algorand.android.module.account.core.component.polling.accountdetail.data.model.AccountCacheRefreshRequestBody
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.repository.AccountBlockPollingRepository
import javax.inject.Inject

internal class AccountBlockPollingRepositoryImpl @Inject constructor(
    private val mobileAlgorandApi: MobileAlgorandApi
) : AccountBlockPollingRepository {

    override suspend fun isAccountUpdateRequired(
        localAccountAddresses: List<String>,
        latestKnownRound: Long?
    ): Result<Boolean> {
        val body = AccountCacheRefreshRequestBody(localAccountAddresses, latestKnownRound)
        return try {
            val shouldRefresh = mobileAlgorandApi.shouldRefreshAccountCache(body).shouldRefresh
            if (shouldRefresh == null) {
                Result.failure(IllegalArgumentException())
            } else {
                Result.success(shouldRefresh)
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
