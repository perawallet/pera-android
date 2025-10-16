/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.data.service

import com.algorand.wallet.asset.data.model.AssetResponse
import com.algorand.wallet.asset.data.model.GetAssetsByIdsRequestBody
import com.algorand.wallet.foundation.network.model.Pagination
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AssetDetailRetrofitApiService {

    @GET("v1/assets/")
    suspend fun getAssetsByIds(
        @Query("asset_ids", encoded = true) assetIdsList: String,
        @Query("include_deleted") includeDeleted: Boolean? = null
    ): Pagination<AssetResponse>

    @GET("v1/assets/{asset_id}/")
    suspend fun getAssetDetail(
        @Path("asset_id") nftAssetId: Long
    ): AssetResponse

    @POST("v2/assets/")
    suspend fun getAssetsByIdsV2(@Body requestBody: GetAssetsByIdsRequestBody): Pagination<AssetResponse>

    @GET("v2/assets/{asset_id}/")
    suspend fun getAssetDetailV2(
        @Query("device_id") deviceId: Long,
        @Path("asset_id") nftAssetId: Long
    ): AssetResponse
}
