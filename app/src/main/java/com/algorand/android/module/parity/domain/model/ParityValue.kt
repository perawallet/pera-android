package com.algorand.android.module.parity.domain.model

import com.algorand.android.module.formatting.formatAsCurrency
import java.math.BigDecimal

data class ParityValue(
    val amountAsCurrency: BigDecimal,
    val selectedCurrencySymbol: String
) {
    fun getFormattedValue(isCompact: Boolean = false, minValueToDisplayExactAmount: BigDecimal? = null): String {
        return if (minValueToDisplayExactAmount?.compareTo(amountAsCurrency) == 1) {
            minValueToDisplayExactAmount.formatAsLowerThanMinCurrency(selectedCurrencySymbol)
        } else {
            amountAsCurrency.formatAsCurrency(selectedCurrencySymbol, isCompact = isCompact)
        }
    }

    fun getFormattedCompactValue(): String {
        return amountAsCurrency.formatAsCurrency(selectedCurrencySymbol, isCompact = true, isFiat = true)
    }

    private fun BigDecimal.formatAsLowerThanMinCurrency(symbol: String): String {
        return StringBuilder(symbol).append("<").append(this.stripTrailingZeros().toPlainString()).toString()
    }
}
