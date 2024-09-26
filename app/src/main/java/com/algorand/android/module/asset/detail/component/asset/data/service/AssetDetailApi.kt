package com.algorand.android.module.asset.detail.component.asset.data.service

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.Pagination
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AssetDetailApi {

    @GET("v1/assets/")
    suspend fun getAssetsByIds(
        @Query("asset_ids", encoded = true) assetIdsList: String,
        @Query("include_deleted") includeDeleted: Boolean? = null
    ): Pagination<AssetResponse>

    @GET("v1/assets/{asset_id}/")
    suspend fun getAssetDetail(
        @Path("asset_id") nftAssetId: Long
    ): Response<AssetResponse>
}
