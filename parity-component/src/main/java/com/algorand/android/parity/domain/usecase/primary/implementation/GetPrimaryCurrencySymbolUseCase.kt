package com.algorand.android.parity.domain.usecase.primary.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.parity.domain.repository.ParityRepository
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import javax.inject.Inject

internal class GetPrimaryCurrencySymbolUseCase @Inject constructor(
    private val parityRepository: ParityRepository
) : GetPrimaryCurrencySymbol {

    override fun invoke(): String? {
        return (parityRepository.getSelectedCurrencyDetail() as? CacheResult.Success)?.data?.currencySymbol
    }
}
