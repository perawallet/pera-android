package com.algorand.android.currency.domain.usecase

import com.algorand.android.currency.domain.model.CurrencyOption
import com.algorand.android.foundation.PeraResult

fun interface GetCurrencyOptionList {
    suspend operator fun invoke(): PeraResult<List<CurrencyOption>>
}
