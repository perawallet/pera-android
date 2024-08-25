package com.algorand.android.parity.domain.usecase.implementation

import com.algorand.android.parity.domain.usecase.*
import com.algorand.android.parity.domain.util.ParityConstants.SAFE_PARITY_DIVISION_DECIMALS
import java.math.*
import javax.inject.Inject

internal class GetAlgoToUsdConversionRateUseCase @Inject constructor(
    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate
) : GetAlgoToUsdConversionRate {

    override fun invoke(): BigDecimal {
        val usdToAlgoConversionRate = getUsdToAlgoConversionRate()
        return if (usdToAlgoConversionRate != BigDecimal.ZERO) {
            BigDecimal.ONE.divide(usdToAlgoConversionRate, SAFE_PARITY_DIVISION_DECIMALS, RoundingMode.UP)
        } else {
            BigDecimal.ZERO
        }
    }
}
