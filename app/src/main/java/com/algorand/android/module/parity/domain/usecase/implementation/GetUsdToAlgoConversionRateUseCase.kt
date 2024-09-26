package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.module.parity.domain.util.ParityConstants.SAFE_PARITY_DIVISION_DECIMALS
import java.math.*
import javax.inject.Inject

internal class GetUsdToAlgoConversionRateUseCase @Inject constructor(
    private val parityRepository: ParityRepository
) : GetUsdToAlgoConversionRate {

    override fun invoke(): BigDecimal {
        return (parityRepository.getSelectedCurrencyDetail() as? CacheResult.Success?)?.data?.run {
            if (algoToSelectedCurrencyConversionRate == BigDecimal.ZERO ||
                algoToSelectedCurrencyConversionRate == null
            ) {
                BigDecimal.ZERO
            } else {
                usdToSelectedCurrencyConversionRate?.divide(
                    algoToSelectedCurrencyConversionRate,
                    SAFE_PARITY_DIVISION_DECIMALS,
                    RoundingMode.UP
                )
            }
        } ?: BigDecimal.ZERO
    }
}
