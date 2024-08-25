package com.algorand.android.parity.domain.usecase

import com.algorand.android.parity.domain.model.ParityValue
import java.math.*

internal interface CalculateParityValue {
    operator fun invoke(
        assetUsdValue: BigDecimal,
        assetDecimals: Int,
        amount: BigInteger,
        conversionRate: BigDecimal,
        currencySymbol: String
    ): ParityValue
}
