package com.algorand.android.module.currency.data.service

import com.algorand.android.module.currency.data.model.CurrencyOptionResponse
import retrofit2.Response
import retrofit2.http.GET

internal interface CurrencyApiService {
    @GET("v1/currencies/")
    suspend fun getCurrencies(): Response<List<CurrencyOptionResponse>>
}
