package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.module.caching.CacheResult
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import javax.inject.Inject

internal class GetPrimaryCurrencySymbolUseCase @Inject constructor(
    private val parityRepository: ParityRepository
) : GetPrimaryCurrencySymbol {

    override fun invoke(): String? {
        return (parityRepository.getSelectedCurrencyDetail() as? CacheResult.Success)?.data?.currencySymbol
    }
}
