package com.algorand.android.ui.asset.lite.extension

import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.asset.domain.model.AssetLite
import java.math.BigInteger

fun AssetLite.getFormattedAmount(): String {
    return amount.formatAmount(decimal)
}

fun AssetLite.getFormattedCompactAmount(): String {
    return amount.formatAmount(decimal, isCompact = true)
}

fun AssetLite.isAmountInSelectedCurrencyVisible(): Boolean {
    return usdValue != null && amount isGreaterThan BigInteger.ZERO
}


