package com.algorand.android.module.parity.domain.usecase.secondary.implementation

import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.module.parity.domain.usecase.secondary.GetUsdToSecondaryCurrencyConversionRate
import java.math.BigDecimal
import javax.inject.Inject

internal class GetUsdToSecondaryCurrencyConversionRateUseCase @Inject constructor(
    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetUsdToSecondaryCurrencyConversionRate {

    override fun invoke(): BigDecimal {
        return if (isPrimaryCurrencyAlgo()) {
            BigDecimal.ONE
        } else {
            getUsdToAlgoConversionRate()
        }
    }
}
