package com.algorand.android.module.parity.domain.usecase

import com.algorand.android.module.parity.domain.model.ParityValue
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
