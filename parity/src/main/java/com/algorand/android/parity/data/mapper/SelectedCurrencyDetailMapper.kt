package com.algorand.android.parity.data.mapper

import com.algorand.android.parity.data.model.CurrencyDetailResponse
import com.algorand.android.parity.domain.model.SelectedCurrencyDetail

internal interface SelectedCurrencyDetailMapper {
    operator fun invoke(response: CurrencyDetailResponse, isPrimaryCurrencyAlgo: Boolean): SelectedCurrencyDetail
}
