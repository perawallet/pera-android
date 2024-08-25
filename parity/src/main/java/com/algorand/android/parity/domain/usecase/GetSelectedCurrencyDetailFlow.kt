package com.algorand.android.parity.domain.usecase

import com.algorand.android.caching.CacheResult
import com.algorand.android.parity.domain.model.SelectedCurrencyDetail
import kotlinx.coroutines.flow.Flow

fun interface GetSelectedCurrencyDetailFlow {
    operator fun invoke(): Flow<CacheResult<SelectedCurrencyDetail>?>
}
