/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.block.data.service

import com.algorand.android.module.block.data.model.ShouldRefreshRequestBody
import com.algorand.android.module.block.data.model.ShouldRefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface BlockPollingApiService {

    @POST("v1/algorand-indexer/should-refresh/")
    suspend fun shouldRefresh(@Body body: ShouldRefreshRequestBody): Response<ShouldRefreshResponse>
}