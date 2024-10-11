package com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.service

import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.model.AssetInboxOneAccountPaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface AssetInboxOneAccountApiService {

    @GET("v1/asa-inboxes/requests/{address}/")
    suspend fun getAssetInboxOneAccountRequests(
        @Path("address") address: String
    ): Response<AssetInboxOneAccountPaginatedResponse>

    suspend fun getAssetInboxOneAccountRequestsMore(@Url url: String):
            Response<AssetInboxOneAccountPaginatedResponse>
}
