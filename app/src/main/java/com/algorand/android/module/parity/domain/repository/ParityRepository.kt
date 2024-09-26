package com.algorand.android.module.parity.domain.repository

import com.algorand.android.module.caching.CacheResult
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import kotlinx.coroutines.flow.Flow

internal interface ParityRepository {
    suspend fun fetchAndCacheParity(currencyPreference: String): CacheResult<SelectedCurrencyDetail>
    fun clearSelectedCurrencyDetailCache()
    fun getSelectedCurrencyDetailFlow(): Flow<CacheResult<SelectedCurrencyDetail>?>
    fun getSelectedCurrencyDetail(): CacheResult<SelectedCurrencyDetail>?
}
