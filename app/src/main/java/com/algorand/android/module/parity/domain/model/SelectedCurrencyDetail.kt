package com.algorand.android.module.parity.domain.model

import java.math.BigDecimal

data class SelectedCurrencyDetail(
    val currencyId: String,
    val currencyName: String?,
    val currencySymbol: String?,
    val algoToSelectedCurrencyConversionRate: BigDecimal?,
    val usdToSelectedCurrencyConversionRate: BigDecimal?
)
