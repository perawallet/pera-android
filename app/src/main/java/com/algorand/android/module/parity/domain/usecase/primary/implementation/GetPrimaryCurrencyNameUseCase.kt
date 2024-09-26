package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.module.caching.CacheResult.Success
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyName
import javax.inject.Inject

internal class GetPrimaryCurrencyNameUseCase @Inject constructor(
    private val parityRepository: ParityRepository,
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId
) : GetPrimaryCurrencyName {
    override fun invoke(): String {
        val currencySymbol = (parityRepository.getSelectedCurrencyDetail() as? Success)?.data?.currencySymbol
        return currencySymbol ?: getPrimaryCurrencyId()
    }
}
