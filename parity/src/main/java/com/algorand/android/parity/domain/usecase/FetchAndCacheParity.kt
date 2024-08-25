package com.algorand.android.parity.domain.usecase

import com.algorand.android.caching.CacheResult
import com.algorand.android.parity.domain.model.SelectedCurrencyDetail

internal interface FetchAndCacheParity {
    suspend operator fun invoke(): CacheResult<SelectedCurrencyDetail>
}
