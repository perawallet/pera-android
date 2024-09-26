package com.algorand.android.module.currency.domain.usecase.implementation

import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import javax.inject.Inject

internal class IsPrimaryCurrencyAlgoUseCase @Inject constructor(
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId
) : IsPrimaryCurrencyAlgo {

    override fun invoke(): Boolean = getPrimaryCurrencyId() == Currency.ALGO.id
}
