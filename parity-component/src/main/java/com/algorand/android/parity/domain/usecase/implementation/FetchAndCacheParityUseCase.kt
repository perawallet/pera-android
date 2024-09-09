package com.algorand.android.parity.domain.usecase.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.currency.domain.usecase.*
import com.algorand.android.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.parity.domain.repository.ParityRepository
import com.algorand.android.parity.domain.usecase.FetchAndCacheParity
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
