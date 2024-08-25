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

package com.algorand.android.network

import com.algorand.android.models.AssetSearchResponse
import com.algorand.android.models.AssetSupportRequest
import com.algorand.android.models.Pagination
import com.algorand.android.models.TrackTransactionRequest
import com.algorand.android.modules.assets.addition.base.ui.BaseAddAssetViewModel.Companion.SEARCH_RESULT_LIMIT
import com.algorand.android.modules.nftdomain.data.model.NftDomainSearchResponse
import com.algorand.android.modules.notification.data.model.LastSeenNotificationRequest
import com.algorand.android.modules.notification.data.model.LastSeenNotificationResponse
import com.algorand.android.modules.notification.data.model.NotificationStatusResponse
import com.algorand.android.modules.walletconnect.connectionrequest.data.model.GetWCDomainScammerStateResponse
import com.algorand.android.modules.webimport.loading.data.model.ImportBackupResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface MobileAlgorandApi {

    @POST("v1/transactions/")
    suspend fun trackTransaction(@Body trackTransactionRequest: TrackTransactionRequest): Response<Unit>

    @POST("v1/asset-requests/")
    suspend fun postAssetSupportRequest(@Body assetSupportRequest: AssetSupportRequest): Response<Unit>

    @GET("v1/devices/{device_id}/notification-status/")
    suspend fun getNotificationStatus(
        @Path("device_id") deviceId: String
    ): Response<NotificationStatusResponse>

    @PUT("v1/devices/{device_id}/update-last-seen-notification/")
    suspend fun putLastSeenNotification(
        @Path("device_id") deviceId: String,
        @Body lastSeenRequest: LastSeenNotificationRequest
    ): Response<LastSeenNotificationResponse>

    @GET("v1/assets/search/")
    suspend fun getAssets(
        @Query("paginator") paginator: String? = "cursor",
        @Query("q") assetQuery: String?,
        @Query("offset") offset: Long = 0,
        @Query("limit") limit: Int = SEARCH_RESULT_LIMIT,
        @Query("has_collectible") hasCollectible: Boolean? = null,
        @Query("available_on_discover_mobile") availableOnDiscoverMobile: Boolean? = null
    ): Response<Pagination<AssetSearchResponse>>

    @GET
    suspend fun getAssetsMore(@Url url: String): Response<Pagination<AssetSearchResponse>>

    @GET("v1/name-services/search/")
    suspend fun getNftDomainAccountAddresses(
        @Query("name") name: String
    ): Response<NftDomainSearchResponse>

    @Streaming
    @GET("v1/accounts/{address}/export-history/")
    suspend fun getExportHistory(
        @Path("address") address: String,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?
    ): Response<ResponseBody>

    @GET("v1/discover/assets/trending/")
    suspend fun getTrendingAssets(): Response<List<AssetSearchResponse>>

    @GET("v1/backups/{id}/")
    suspend fun getBackup(
        @Path("id") id: String
    ): Response<ImportBackupResponse>

    @GET("v1/is-scammer-domain/")
    suspend fun getWCDomainScammerState(
        @Query("domain") domain: String
    ): Response<GetWCDomainScammerStateResponse>
}
