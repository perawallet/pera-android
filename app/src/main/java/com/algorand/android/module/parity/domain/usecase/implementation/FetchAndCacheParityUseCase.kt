package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.caching.CacheResult
import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.module.parity.domain.usecase.FetchAndCacheParity
import javax.inject.Inject

internal class FetchAndCacheParityUseCase @Inject constructor(
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val parityRepository: ParityRepository
) : FetchAndCacheParity {

    override suspend fun invoke(): CacheResult<SelectedCurrencyDetail> {
        return parityRepository.fetchAndCacheParity(getCurrencyToFetch())
    }

    private fun getCurrencyToFetch(): String {
        return if (isPrimaryCurrencyAlgo()) {
            CURRENCY_TO_FETCH_WHEN_ALGO_IS_SELECTED
        } else {
            getPrimaryCurrencyId()
        }
    }

    companion object {
        private val CURRENCY_TO_FETCH_WHEN_ALGO_IS_SELECTED = Currency.USD.id
    }
}
