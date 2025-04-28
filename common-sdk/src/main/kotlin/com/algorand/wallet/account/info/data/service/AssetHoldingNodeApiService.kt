package com.algorand.wallet.account.info.data.service

import com.algorand.wallet.account.info.data.model.AssetHoldingNodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal interface AssetHoldingNodeApiService {

    @GET("v2/accounts/{address}/assets/{asset_id}")
    suspend fun getAssetHolding(
        @Path("address") address: String,
        @Path("asset_id") assetId: Long
    ): Response<AssetHoldingNodeResponse>
}
