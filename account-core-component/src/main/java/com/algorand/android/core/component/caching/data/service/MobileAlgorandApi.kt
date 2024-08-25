package com.algorand.android.core.component.caching.data.service

import com.algorand.android.core.component.polling.accountdetail.data.model.*
import retrofit2.http.*

internal interface MobileAlgorandApi {

    @POST("v1/algorand-indexer/should-refresh/")
    suspend fun shouldRefreshAccountCache(
        @Body body: AccountCacheRefreshRequestBody
    ): AccountCacheRefreshResponse
}
