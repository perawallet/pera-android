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

package com.algorand.wallet.cards.data.service

import com.algorand.wallet.cards.data.model.CountryAvailabilityResponse
import com.algorand.wallet.cards.data.model.FundAddressResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CardApiService {

    @GET("v1/cards/fund-addresses/")
    suspend fun getFundAddresses(@Query("addresses") addresses: String): FundAddressResultResponse

    @GET("/v1/cards/country-availability-request/")
    suspend fun isCountryAvailable(@Query("address") addresses: String): CountryAvailabilityResponse
}
