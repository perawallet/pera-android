package com.algorand.android.module.parity.domain.usecase.secondary

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.*
import com.algorand.android.module.parity.domain.usecase.secondary.implementation.GetSecondaryAlgoParityValueUseCase
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

class GetSecondaryAlgoParityValueUseCaseTest {

    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate = mock()
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate = mock()
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol = mock()
    private val calculateParityValue: CalculateParityValue = mock()

    private val sut = GetSecondaryAlgoParityValueUseCase(
        getAlgoToUsdConversionRate,
        getUsdToSecondaryCurrencyConversionRate,
        getSecondaryCurrencySymbol,
        calculateParityValue
    )

    @Test
    fun `EXPECT ParityValue`() {
        whenever(getAlgoToUsdConversionRate()).thenReturn(ALGO_TO_USD_CONVERSION_RATE)
        whenever(getUsdToSecondaryCurrencyConversionRate()).thenReturn(USD_TO_SECONDARY_CURRENCY_CONVERSION_RATE)
        whenever(getSecondaryCurrencySymbol()).thenReturn(SECONDARY_CURRENCY_SYMBOL)
        whenever(
            calculateParityValue(
                assetUsdValue = ALGO_TO_USD_CONVERSION_RATE,
                assetDecimals = ALGO_DECIMAL,
                amount = ALGO_AMOUNT,
                conversionRate = USD_TO_SECONDARY_CURRENCY_CONVERSION_RATE,
                currencySymbol = SECONDARY_CURRENCY_SYMBOL
            )
        ).thenReturn(PARITY_VALUE)

        val result = sut(ALGO_AMOUNT)

        assertEquals(PARITY_VALUE, result)
    }

    companion object {
        private val ALGO_TO_USD_CONVERSION_RATE = fixtureOf<BigDecimal>()
        private val USD_TO_SECONDARY_CURRENCY_CONVERSION_RATE = fixtureOf<BigDecimal>()
        private val SECONDARY_CURRENCY_SYMBOL = fixtureOf<String>()
        private val ALGO_AMOUNT = fixtureOf<BigInteger>()
        private val PARITY_VALUE = fixtureOf<ParityValue>()
        private val ALGO_DECIMAL = 6
    }
}
