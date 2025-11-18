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
