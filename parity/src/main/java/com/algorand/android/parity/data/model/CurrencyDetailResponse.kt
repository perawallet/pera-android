package com.algorand.android.parity.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

internal data class CurrencyDetailResponse(
    @SerializedName("currency_id")
    val id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("exchange_price")
    val exchangePrice: String?,

    @SerializedName("symbol")
    val symbol: String?,

    @SerializedName("usd_value")
    val usdValue: BigDecimal?,

    @SerializedName("last_updated_at")
    val lastUpdateTimestamp: String?, // "2021-12-15 11:21:31"

    @SerializedName("s")
    val source: String?
)
