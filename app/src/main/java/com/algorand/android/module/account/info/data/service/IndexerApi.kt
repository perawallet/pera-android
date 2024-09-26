package com.algorand.android.module.account.info.data.service

import com.algorand.android.module.account.info.data.model.AccountAssetsResponse
import com.algorand.android.module.account.info.data.model.AccountInformationResponse
import com.algorand.android.module.account.info.data.model.RekeyedAccountsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface IndexerApi {

    @GET("v2/accounts/{public_key}")
    suspend fun getAccountInformation(
        @Path("public_key") publicKey: String,
        @Query("exclude", encoded = true) excludes: String,
        @Query("include-all") includeClosedAccounts: Boolean = false
    ): Response<AccountInformationResponse>

    @GET("v2/accounts")
    suspend fun getRekeyedAccounts(
        @Query("auth-addr") rekeyAdminAddress: String
    ): Response<RekeyedAccountsResponse>

    @GET("v2/accounts/{address}/assets")
    suspend fun getAccountAssets(
        @Path("address") address: String,
        @Query("limit") limit: Int,
        @Query("next") nextToken: String? = null
    ): Response<AccountAssetsResponse>
}
