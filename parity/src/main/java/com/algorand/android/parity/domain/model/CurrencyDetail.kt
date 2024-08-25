package com.algorand.android.parity.domain.model

import java.math.BigDecimal

data class CurrencyDetail(
    val id: String,
    val name: String?,
    val exchangePrice: String?,
    val symbol: String?,
    val usdValue: BigDecimal?,
    val lastUpdateTimestamp: String?,
    val source: String?
)
