package com.algorand.android.currency.domain.usecase.implementation

import com.algorand.android.currency.domain.model.*
import com.algorand.android.currency.domain.usecase.*
import javax.inject.Inject

internal class GetSelectedCurrencyImpl @Inject constructor(
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetSelectedCurrency {

    override fun invoke(): SelectedCurrency {
        return SelectedCurrency(
            primaryCurrencyId = getPrimaryCurrencyId(),
            secondaryCurrencyId = getSecondaryCurrencyId()
        )
    }

    // If Primary currency;
    //  is Algo -> App always display the $ as secondary currency.
    //  is other than Algo -> App always display Algo as secondary currency
    private fun getSecondaryCurrencyId(): String {
        return if (isPrimaryCurrencyAlgo()) {
            Currency.USD
        } else {
            Currency.ALGO
        }.id
    }
}
