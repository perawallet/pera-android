package com.algorand.android.parity.domain.usecase.primary.implementation

import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.CalculateParityValue
import com.algorand.android.parity.domain.usecase.primary.*
import com.algorand.android.testutil.fixtureOf
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

class GetPrimaryCurrencyAssetParityValueUseCaseTest {

    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol = mock()
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate = mock()
    private val calculateParityValue: CalculateParityValue = mock()

    private val sut = GetPrimaryCurrencyAssetParityValueUseCase(
        getPrimaryCurrencySymbol,
        getUsdToPrimaryCurrencyConversionRate,
        calculateParityValue
    )

    @Test
    fun `EXPECT ParityValue with primary currency symbol WHEN symbol is not null`() {
        whenever(getPrimaryCurrencySymbol()).thenReturn(PRIMARY_CURRENCY_SYMBOL)
        whenever(getUsdToPrimaryCurrencyConversionRate()).thenReturn(USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE)
        whenever(
            calculateParityValue(
                USD_VALUE,
                DECIMALS,
                AMOUNT,
                USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE,
                PRIMARY_CURRENCY_SYMBOL
            )
        ).thenReturn(PARITY_VALUE.copy(selectedCurrencySymbol = PRIMARY_CURRENCY_SYMBOL))

        val result = sut(USD_VALUE, DECIMALS, AMOUNT)

        assert(result.selectedCurrencySymbol == PRIMARY_CURRENCY_SYMBOL)
    }

    @Test
    fun `EXPECT ParityValue with empty string WHEN symbol is null`() {
        whenever(getPrimaryCurrencySymbol()).thenReturn(null)
        whenever(getUsdToPrimaryCurrencyConversionRate()).thenReturn(USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE)
        whenever(
            calculateParityValue(
                USD_VALUE,
                DECIMALS,
                AMOUNT,
                USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE,
                ""
            )
        ).thenReturn(PARITY_VALUE.copy(selectedCurrencySymbol = ""))

        val result = sut(USD_VALUE, DECIMALS, AMOUNT)

        assert(result.selectedCurrencySymbol == "")
    }

    companion object {
        private val PRIMARY_CURRENCY_SYMBOL = fixtureOf<String>()
        private val USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE = fixtureOf<BigDecimal>()
        private val USD_VALUE = fixtureOf<BigDecimal>()
        private val DECIMALS = fixtureOf<Int>()
        private val AMOUNT = fixtureOf<BigInteger>()
        private val PARITY_VALUE = fixtureOf<ParityValue>()
    }
}
