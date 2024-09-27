package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.module.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.module.parity.domain.util.ParityConstants.SAFE_PARITY_DIVISION_DECIMALS
import java.math.BigDecimal
import java.math.RoundingMode
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
