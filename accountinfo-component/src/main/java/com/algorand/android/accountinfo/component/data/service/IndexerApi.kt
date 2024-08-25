package com.algorand.android.accountinfo.component.data.service

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.accountinfo.component.data.model.RekeyedAccountsResponse
import retrofit2.Response
import retrofit2.http.*

internal interface IndexerApi {

    @GET("v2/accounts/{public_key}?exclude=created-assets,created-apps")
    suspend fun getAccountInformation(
        @Path("public_key") publicKey: String,
        @Query("include-all") includeClosedAccounts: Boolean = false
    ): Response<AccountInformationResponse>

    @GET("v2/accounts")
    suspend fun getRekeyedAccounts(
        @Query("auth-addr") rekeyAdminAddress: String
    ): Response<RekeyedAccountsResponse>
}
