package com.algorand.android.module.parity.data.mapper

import com.algorand.android.currency.domain.model.Currency
import com.algorand.android.module.parity.data.model.CurrencyDetailResponse
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.util.ParityConstants.SAFE_PARITY_DIVISION_DECIMALS
import java.math.*
import javax.inject.Inject

internal class SelectedCurrencyDetailMapperImpl @Inject constructor() : SelectedCurrencyDetailMapper {

    override fun invoke(response: CurrencyDetailResponse, isPrimaryCurrencyAlgo: Boolean): SelectedCurrencyDetail {
        return SelectedCurrencyDetail(
            getSelectedCurrencyId(response, isPrimaryCurrencyAlgo),
            getSelectedCurrencyName(response, isPrimaryCurrencyAlgo),
            getSelectedCurrencySymbol(response, isPrimaryCurrencyAlgo),
            getAlgoToSelectedCurrencyParityValue(response, isPrimaryCurrencyAlgo),
            getUsdToSelectedCurrencyParityValue(response, isPrimaryCurrencyAlgo)
        )
    }

    private fun getSelectedCurrencyId(
        currencyDetailResponse: CurrencyDetailResponse,
        isSelectedCurrencyAlgo: Boolean
    ): String {
        return if (isSelectedCurrencyAlgo) Currency.ALGO.id else currencyDetailResponse.id
    }

    private fun getSelectedCurrencyName(
        currencyDetailResponse: CurrencyDetailResponse,
        isSelectedCurrencyAlgo: Boolean
    ): String {
        return if (isSelectedCurrencyAlgo) Currency.ALGO.id else currencyDetailResponse.name.orEmpty()
    }

    private fun getSelectedCurrencySymbol(
        currencyDetailResponse: CurrencyDetailResponse,
        isSelectedCurrencyAlgo: Boolean
    ): String {
        return if (isSelectedCurrencyAlgo) Currency.ALGO.symbol else currencyDetailResponse.symbol.orEmpty()
    }

    private fun getAlgoToSelectedCurrencyParityValue(
        currencyDetailResponse: CurrencyDetailResponse,
        isSelectedCurrencyAlgo: Boolean
    ): BigDecimal? {
        return if (isSelectedCurrencyAlgo) {
            BigDecimal.ONE
        } else {
            currencyDetailResponse.exchangePrice?.toBigDecimalOrNull()
        }
    }

    private fun getUsdToSelectedCurrencyParityValue(
        currencyDetailResponse: CurrencyDetailResponse,
        isSelectedCurrencyAlgo: Boolean
    ): BigDecimal? {
        with(currencyDetailResponse) {
            return if (isSelectedCurrencyAlgo) {
                val algoToCurrencyConversionRate = exchangePrice?.toBigDecimalOrNull()
                if (algoToCurrencyConversionRate == BigDecimal.ZERO || algoToCurrencyConversionRate == null) {
                    BigDecimal.ZERO
                } else {
                    usdValue?.divide(algoToCurrencyConversionRate, SAFE_PARITY_DIVISION_DECIMALS, RoundingMode.UP)
                }
            } else {
                currencyDetailResponse.usdValue
            }
        }
    }
}
