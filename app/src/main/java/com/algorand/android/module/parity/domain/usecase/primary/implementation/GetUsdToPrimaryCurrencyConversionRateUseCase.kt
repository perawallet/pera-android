package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.primary.GetUsdToPrimaryCurrencyConversionRate
import java.math.BigDecimal
import javax.inject.Inject

internal class GetUsdToPrimaryCurrencyConversionRateUseCase @Inject constructor(
    private val parityRepository: ParityRepository,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetUsdToPrimaryCurrencyConversionRate {

    override fun invoke(): BigDecimal {
        val cachedData = (parityRepository.getSelectedCurrencyDetail() as? CacheResult.Success)?.data
        return if (isPrimaryCurrencyAlgo()) {
            cachedData?.usdToSelectedCurrencyConversionRate
        } else {
            cachedData?.algoToSelectedCurrencyConversionRate
        } ?: BigDecimal.ZERO
    }
}
