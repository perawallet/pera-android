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

package com.algorand.android.swap.data.service

import com.algorand.android.swap.data.model.AvailableSwapAssetListResponse
import com.algorand.android.swap.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.android.swap.data.model.CreateSwapQuoteTransactionsResponse
import com.algorand.android.swap.data.model.PeraFeeRequestBody
import com.algorand.android.swap.data.model.PeraFeeResponse
import com.algorand.android.swap.data.model.SwapQuoteExceptionRequestBody
import com.algorand.android.swap.data.model.SwapQuoteRequestBody
import com.algorand.android.swap.data.model.SwapQuoteResultResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AssetSwapApi {

    @POST("v1/dex-swap/quotes/")
    suspend fun getSwapQuote(
        @Body requestBody: SwapQuoteRequestBody
    ): Response<SwapQuoteResultResponse>

    @PATCH("v1/dex-swap/quotes/{quote_id}/")
    suspend fun putSwapQuoteException(
        @Path("quote_id") quoteId: Long,
        @Body swapQuoteExceptionRequestBody: SwapQuoteExceptionRequestBody
    ): Response<Unit>

    @POST("v1/dex-swap/calculate-pera-fee/")
    suspend fun getPeraFee(
        @Body requestBody: PeraFeeRequestBody
    ): Response<PeraFeeResponse>

    @POST("v1/dex-swap/prepare-transactions/")
    suspend fun getQuoteTransactions(
        @Body requestBody: CreateSwapQuoteTransactionsRequestBody
    ): Response<CreateSwapQuoteTransactionsResponse>

    @GET("v1/dex-swap/available-assets/")
    suspend fun getAvailableSwapAssetList(
        @Query("asset_in_id") assetId: Long,
        @Query("providers") providersAsCsv: String,
        @Query("q") query: String?
    ): Response<AvailableSwapAssetListResponse>
}
