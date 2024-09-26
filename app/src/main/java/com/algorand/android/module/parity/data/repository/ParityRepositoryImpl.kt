package com.algorand.android.module.parity.data.repository

import com.algorand.android.caching.CacheResult
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.data.mapper.SelectedCurrencyDetailMapper
import com.algorand.android.module.parity.data.model.CurrencyDetailResponse
import com.algorand.android.module.parity.data.service.ParityApiService
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import com.algorand.android.network_utils.requestWithHipoErrorHandler
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class ParityRepositoryImpl @Inject constructor(
    private val parityApiService: ParityApiService,
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val selectedCurrencyDetailSingleLocalCache: SingleInMemoryLocalCache<SelectedCurrencyDetail>,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val selectedCurrencyDetailMapper: SelectedCurrencyDetailMapper
) : ParityRepository {

    override suspend fun fetchAndCacheParity(currencyPreference: String): CacheResult<SelectedCurrencyDetail> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            parityApiService.getCurrencyDetail(currencyPreference)
        }.use(
            onSuccess = { response ->
                val cacheResult = mapToSuccessResult(response)
                selectedCurrencyDetailSingleLocalCache.put(cacheResult)
                cacheResult
            },
            onFailed = { exception, code ->
                val errorCacheResult = CacheResult.Error.create<SelectedCurrencyDetail>(exception, code = code)
                selectedCurrencyDetailSingleLocalCache.put(errorCacheResult)
                errorCacheResult
            }
        )
    }

    override fun clearSelectedCurrencyDetailCache() {
        selectedCurrencyDetailSingleLocalCache.clear()
    }

    override fun getSelectedCurrencyDetailFlow(): Flow<CacheResult<SelectedCurrencyDetail>?> {
        return selectedCurrencyDetailSingleLocalCache.cacheFlow
    }

    override fun getSelectedCurrencyDetail(): CacheResult<SelectedCurrencyDetail>? {
        return selectedCurrencyDetailSingleLocalCache.getOrNull()
    }

    private fun mapToSuccessResult(response: CurrencyDetailResponse): CacheResult<SelectedCurrencyDetail> {
        val selectedCurrencyDetail = selectedCurrencyDetailMapper(response, isPrimaryCurrencyAlgo())
        return CacheResult.Success.create(selectedCurrencyDetail)
    }
}
