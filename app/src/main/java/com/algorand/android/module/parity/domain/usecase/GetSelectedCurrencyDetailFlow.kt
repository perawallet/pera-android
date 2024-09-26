package com.algorand.android.module.parity.domain.usecase

import com.algorand.android.module.caching.CacheResult
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import kotlinx.coroutines.flow.Flow

fun interface GetSelectedCurrencyDetailFlow {
    operator fun invoke(): Flow<CacheResult<SelectedCurrencyDetail>?>
}
