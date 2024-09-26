package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import java.math.*
import javax.inject.Inject

internal class CalculateParityValueUseCase @Inject constructor() : CalculateParityValue {

    override fun invoke(
        assetUsdValue: BigDecimal,
        assetDecimals: Int,
        amount: BigInteger,
        conversionRate: BigDecimal,
        currencySymbol: String
    ): ParityValue {
        val amountInSelectedCurrency = amount.toBigDecimal().movePointLeft(assetDecimals)
            .multiply(conversionRate)
            .multiply(assetUsdValue)
        return ParityValue(amountInSelectedCurrency, currencySymbol)
    }
}
