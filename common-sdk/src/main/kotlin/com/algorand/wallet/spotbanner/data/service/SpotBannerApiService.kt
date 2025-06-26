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

package com.algorand.wallet.spotbanner.data.service

import com.algorand.wallet.spotbanner.data.model.SpotBannerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

internal interface SpotBannerApiService {

    @GET("v1/devices/{device_id}/spot-banners/")
    suspend fun getSpotBanners(@Path("device_id") deviceId: String): List<SpotBannerResponse>

    @PATCH("v1/devices/{device_id}/spot-banners/{spot_banner_id}/close/")
    suspend fun dismissSpotBanner(
        @Path("device_id") deviceId: String,
        @Path("spot_banner_id") spotBannerId: Long
    ): Response<Unit>
}
