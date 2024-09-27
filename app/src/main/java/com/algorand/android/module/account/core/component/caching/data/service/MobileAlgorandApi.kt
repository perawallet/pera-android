package com.algorand.android.module.account.core.component.caching.data.service

import com.algorand.android.module.account.core.component.polling.accountdetail.data.model.AccountCacheRefreshRequestBody
import com.algorand.android.module.account.core.component.polling.accountdetail.data.model.AccountCacheRefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface MobileAlgorandApi {

    @POST("v1/algorand-indexer/should-refresh/")
    suspend fun shouldRefreshAccountCache(
        @Body body: AccountCacheRefreshRequestBody
    ): AccountCacheRefreshResponse
}
