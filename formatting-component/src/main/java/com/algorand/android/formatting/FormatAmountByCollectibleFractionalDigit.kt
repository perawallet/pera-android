package com.algorand.android.formatting

import java.math.BigInteger

interface FormatAmountByCollectibleFractionalDigit {
    operator fun invoke(
        amount: BigInteger?,
        decimals: Int,
        isDecimalFixed: Boolean = false,
        isCompact: Boolean = false
    ): String
}
