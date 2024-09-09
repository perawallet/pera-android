package com.algorand.android.currency.domain.usecase.implementation

import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.currency.domain.usecase.*
import javax.inject.Inject

internal class IsPrimaryCurrencyAlgoUseCase @Inject constructor(
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId
) : IsPrimaryCurrencyAlgo {

    override fun invoke(): Boolean = getPrimaryCurrencyId() == Currency.ALGO.id
}
