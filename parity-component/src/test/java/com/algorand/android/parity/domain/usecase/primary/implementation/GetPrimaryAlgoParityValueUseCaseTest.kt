package com.algorand.android.parity.domain.usecase.primary.implementation

import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.*
import com.algorand.android.parity.domain.usecase.primary.*
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

class GetPrimaryAlgoParityValueUseCaseTest {

    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate = mock()
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate = mock()
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol = mock()
    private val calculateParityValue: CalculateParityValue = mock()

    private val sut = GetPrimaryAlgoParityValueUseCase(
        getAlgoToUsdConversionRate,
        getUsdToPrimaryCurrencyConversionRate,
        getPrimaryCurrencySymbol,
        calculateParityValue
    )

    @Test
    fun `EXPECT ParityValue`() {
        whenever(getAlgoToUsdConversionRate()).thenReturn(ALGO_TO_USD_CONVERSION_RATE)
        whenever(getUsdToPrimaryCurrencyConversionRate()).thenReturn(USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE)
        whenever(getPrimaryCurrencySymbol()).thenReturn(PRIMARY_CURRENCY_SYMBOL)
        whenever(
            calculateParityValue(
                assetUsdValue = ALGO_TO_USD_CONVERSION_RATE,
                assetDecimals = ALGO_DECIMAL,
                amount = ALGO_AMOUNT,
                conversionRate = USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE,
                currencySymbol = PRIMARY_CURRENCY_SYMBOL
            )
        ).thenReturn(PARITY_VALUE)

        val result = sut(ALGO_AMOUNT)

        assertEquals(PARITY_VALUE, result)
    }

    companion object {
        private val ALGO_TO_USD_CONVERSION_RATE = fixtureOf<BigDecimal>()
        private val USD_TO_PRIMARY_CURRENCY_CONVERSION_RATE = fixtureOf<BigDecimal>()
        private val PRIMARY_CURRENCY_SYMBOL = fixtureOf<String>()
        private val ALGO_AMOUNT = fixtureOf<BigInteger>()
        private val PARITY_VALUE = fixtureOf<ParityValue>()
        private val ALGO_DECIMAL = 6
    }
}
