package com.algorand.android.currency.data.model

import com.google.gson.annotations.SerializedName

internal data class CurrencyOptionResponse(
    @SerializedName("currency_id")
    val id: String,

    @SerializedName("name")
    val name: String
)
