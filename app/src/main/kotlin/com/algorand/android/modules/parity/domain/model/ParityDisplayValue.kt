package com.algorand.android.modules.parity.domain.model

data class ParityDisplayValue(
    val primaryParityValue: ParityValue,
    val secondaryParityValue: ParityValue,
    val formattedAmount: String,
    val formattedCompactAmount: String,
    val isAmountInSelectedCurrencyVisible: Boolean
)
