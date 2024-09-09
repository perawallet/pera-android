package com.algorand.android.parity.data.service

import com.algorand.android.parity.data.model.CurrencyDetailResponse
import retrofit2.Response
import retrofit2.http.*

internal interface ParityApiService {

    @GET("v1/currencies/{currency_id}/")
    suspend fun getCurrencyDetail(
        @Path("currency_id") currencyId: String
    ): Response<CurrencyDetailResponse>
}
