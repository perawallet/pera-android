package com.algorand.android.nameservice.data.service

import com.algorand.android.nameservice.data.model.*
import retrofit2.Response
import retrofit2.http.*

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
