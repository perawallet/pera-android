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

package com.algorand.wallet.swap.data.service

import com.algorand.wallet.swap.data.model.AvailableSwapAssetListResponse
import com.algorand.wallet.swap.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.wallet.swap.data.model.CreateSwapQuoteTransactionsResponse
import com.algorand.wallet.swap.data.model.SwapHistoriesResponse
import com.algorand.wallet.swap.data.model.SwapPairHistoriesResponse
import com.algorand.wallet.swap.data.model.SwapCalculateAmountRequestBody
import com.algorand.wallet.swap.data.model.SwapCalculateAmountResponse
import com.algorand.wallet.swap.data.model.SwapQuoteProvidersResponse
import com.algorand.wallet.swap.data.model.SwapQuoteRequestBody
import com.algorand.wallet.swap.data.model.SwapQuoteResultResponse
import com.algorand.wallet.swap.data.model.SwapUpdateStatusRequestBody
import com.algorand.wallet.swap.data.model.TopSwapPairsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

internal interface SwapApiService {

    @GET("v1/dex-swap/available-assets/")
    suspend fun getAvailableSwapAssetList(
        @Query("asset_in_id") assetId: Long,
        @Query("providers") providersAsCsv: String,
        @Query("q") query: String?
    ): AvailableSwapAssetListResponse

    @POST("v2/dex-swap/quotes/")
    suspend fun getSwapQuote(@Body requestBody: SwapQuoteRequestBody): SwapQuoteResultResponse

    @POST("v1/dex-swap/calculate-swap-amount/")
    suspend fun calculateSwapAmount(@Body requestBody: SwapCalculateAmountRequestBody): SwapCalculateAmountResponse

    @POST("v2/dex-swap/prepare-transactions/")
    suspend fun getQuoteTransactions(
        @Body requestBody: CreateSwapQuoteTransactionsRequestBody
    ): CreateSwapQuoteTransactionsResponse

    @PATCH("v2/dex-swap/swaps/{swap_id}/")
    suspend fun updateSwapStatus(
        @Path("swap_id") swapId: Long,
        @Body swapUpdateStatusRequestBody: SwapUpdateStatusRequestBody
    )

    @GET("v2/dex-swap/providers/")
    suspend fun getSwapQuoteProviders(): SwapQuoteProvidersResponse

    @GET("v2/dex-swap/top-pairs/")
    suspend fun getTopSwapPairs(): TopSwapPairsResponse

    @GET("v2/dex-swap/history/")
    suspend fun getSwapHistory(
        @Query("address") address: String,
        @Query("cursor") cursor: String?,
        @Query("limit") limit: Int,
        @Query("statuses") statuses: String?
    ): SwapHistoriesResponse

    @GET
    suspend fun getSwapHistoryMore(@Url url: String): SwapHistoriesResponse

    @GET("v2/dex-swap/distinct-pairs-history/")
    suspend fun getSwapPairsHistory(
        @Query("address") address: String,
        @Query("statuses") statuses: String?
    ): SwapPairHistoriesResponse
}
