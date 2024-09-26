package com.algorand.android.module.parity.domain.usecase.secondary.implementation

import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import javax.inject.Inject

internal class GetSecondaryCurrencySymbolUseCase @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetSecondaryCurrencySymbol {

    override fun invoke(): String {
        return if (isPrimaryCurrencyAlgo()) Currency.USD.symbol else Currency.ALGO.symbol
    }
}
