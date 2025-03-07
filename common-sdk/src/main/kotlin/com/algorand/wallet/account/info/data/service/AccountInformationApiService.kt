/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.info.data.service

import com.algorand.wallet.account.info.data.model.AccountAssetsResponse
import com.algorand.wallet.account.info.data.model.AccountInformationResponse
import com.algorand.wallet.account.info.data.model.RekeyedAccountsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AccountInformationApiService {

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
