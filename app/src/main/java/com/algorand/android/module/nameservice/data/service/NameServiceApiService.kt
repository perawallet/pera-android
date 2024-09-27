package com.algorand.android.module.nameservice.data.service

import com.algorand.android.module.nameservice.data.model.NameServiceSearchResponse
import com.algorand.android.module.nameservice.data.model.SearchNameServiceRequestBody
import com.algorand.android.module.nameservice.data.model.SearchNameServiceResponse
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
