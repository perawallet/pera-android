package com.algorand.wallet.account.info.data.service

import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal interface AccountFastLookupApiService {
    @GET("v1/accounts/fast-lookup/{address}/")
    suspend fun getAccountFastLookup(
        @Path("address") address: String
    ): Response<AccountFastLookupResponse>
}
