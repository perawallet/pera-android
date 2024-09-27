package com.algorand.android.module.parity.data.mapper

import com.algorand.android.module.parity.data.model.CurrencyDetailResponse
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail

internal interface SelectedCurrencyDetailMapper {
    operator fun invoke(response: CurrencyDetailResponse, isPrimaryCurrencyAlgo: Boolean): SelectedCurrencyDetail
}