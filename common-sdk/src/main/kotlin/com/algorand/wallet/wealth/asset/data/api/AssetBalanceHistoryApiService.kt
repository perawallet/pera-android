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

package com.algorand.wallet.wealth.asset.data.api

import com.algorand.wallet.wealth.asset.data.model.AssetBalanceHistoryResponseResults
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AssetBalanceHistoryApiService {

    @GET("v1/accounts/{account_address}/assets/{asset_id}/balance-history/")
    suspend fun getAssetBalanceHistory(
        @Path("account_address") address: String,
        @Path("asset_id") assetId: Long,
        @Query("period") period: String
    ): AssetBalanceHistoryResponseResults
}
