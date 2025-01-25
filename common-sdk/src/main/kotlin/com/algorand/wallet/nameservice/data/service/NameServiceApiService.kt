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

package com.algorand.wallet.nameservice.data.service

import com.algorand.wallet.nameservice.data.model.NameServiceSearchResponse
import com.algorand.wallet.nameservice.data.model.SearchNameServiceRequestBody
import com.algorand.wallet.nameservice.data.model.SearchNameServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface NameServiceApiService {

    @POST("v1/accounts/names/bulk-read/")
    suspend fun fetchAccountsNameServices(
        @Body body: SearchNameServiceRequestBody
    ): Response<SearchNameServiceResponse>

    @GET("v1/name-services/search/")
    suspend fun getNameServiceAccountAddresses(
        @Query("name") name: String
    ): Response<NameServiceSearchResponse>
}
