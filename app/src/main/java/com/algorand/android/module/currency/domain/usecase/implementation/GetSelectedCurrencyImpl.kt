package com.algorand.android.module.currency.domain.usecase.implementation

import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.module.currency.domain.model.SelectedCurrency
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.currency.domain.usecase.GetSelectedCurrency
import com.algorand.android.module.currency.domain.usecase.IsPrimaryCurrencyAlgo
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
