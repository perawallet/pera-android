package com.algorand.android.module.currency.domain.usecase

import com.algorand.android.module.currency.domain.model.CurrencyOption
import com.algorand.android.module.foundation.PeraResult

fun interface GetCurrencyOptionList {
    suspend operator fun invoke(): PeraResult<List<CurrencyOption>>
}
