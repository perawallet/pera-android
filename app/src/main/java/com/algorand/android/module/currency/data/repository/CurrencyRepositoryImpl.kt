package com.algorand.android.module.currency.data.repository

import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.module.currency.data.service.CurrencyApiService
import com.algorand.android.module.currency.domain.model.Currency
import com.algorand.android.module.currency.domain.model.CurrencyOption
import com.algorand.android.module.currency.domain.repository.CurrencyRepository
import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import com.algorand.android.network_utils.requestWithHipoErrorHandler
import javax.inject.Inject

internal class CurrencyRepositoryImpl @Inject constructor(
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val currencyApiService: CurrencyApiService,
    private val currencyLocalSource: SharedPrefLocalSource<String>
) : CurrencyRepository {

    override fun setPrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>) {
        currencyLocalSource.removeListener(listener)
    }

    override fun removePrimaryCurrencyChangeListener(listener: SharedPrefLocalSource.OnChangeListener<String>) {
        currencyLocalSource.addListener(listener)
    }

    override fun getPrimaryCurrencyPreference(): String {
        return currencyLocalSource.getData(DEFAULT_CURRENCY_PREFERENCE)
    }

    override fun setPrimaryCurrencyPreference(currencyPreference: String) {
        currencyLocalSource.saveData(currencyPreference)
    }

    override suspend fun getCurrencyOptionList(): PeraResult<List<CurrencyOption>> {
        val currencyOptionResponseResult = requestWithHipoErrorHandler(hipoApiErrorHandler) {
            currencyApiService.getCurrencies()
        }
        return currencyOptionResponseResult.map { currencyOptionResponseList ->
            currencyOptionResponseList.map { currencyOptionResponse ->
                CurrencyOption(
                    currencyId = currencyOptionResponse.id,
                    currencyName = currencyOptionResponse.name
                )
            }
        }
    }

    companion object {
        val DEFAULT_CURRENCY_PREFERENCE = Currency.ALGO.id
    }
}
