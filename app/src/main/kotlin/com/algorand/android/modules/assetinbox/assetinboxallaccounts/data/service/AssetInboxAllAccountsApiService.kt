package com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.service

import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.model.AssetInboxAllAccountsListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AssetInboxAllAccountsApiService {

    @GET("v1/asa-inboxes/requests/")
    suspend fun getAssetInboxAllAccountsRequests(
        @Query("addresses") addresses: String
    ): Response<AssetInboxAllAccountsListResponse>
}
