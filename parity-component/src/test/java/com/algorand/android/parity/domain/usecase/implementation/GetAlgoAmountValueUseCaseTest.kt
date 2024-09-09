package com.algorand.android.parity.domain.usecase.implementation

import com.algorand.android.parity.domain.model.*
import com.algorand.android.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryAlgoParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryAlgoParityValue
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.*

class GetAlgoAmountValueUseCaseTest {

    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate = mock()
    private val getPrimaryAlgoParityValue: GetPrimaryAlgoParityValue = mock()
    private val getSecondaryAlgoParityValue: GetSecondaryAlgoParityValue = mock()

    private val sut = GetAlgoAmountValueUseCase(
        getAlgoToUsdConversionRate,
        getPrimaryAlgoParityValue,
        getSecondaryAlgoParityValue
    )

    @Test
    fun `EXPECT AlgoAmountValue`() {
        whenever(getAlgoToUsdConversionRate()).thenReturn(ALGO_TO_USD_CONVERSION_RATE)
        whenever(getPrimaryAlgoParityValue(ALGO_AMOUNT)).thenReturn(PRIMARY_ALGO_PARITY_VALUE)
        whenever(getSecondaryAlgoParityValue(ALGO_AMOUNT)).thenReturn(SECONDARY_ALGO_PARITY_VALUE)

        val result = sut(ALGO_AMOUNT)

        val expected = AlgoAmountValue(
            amount = ALGO_AMOUNT,
            parityValueInSelectedCurrency = PRIMARY_ALGO_PARITY_VALUE,
            parityValueInSecondaryCurrency = SECONDARY_ALGO_PARITY_VALUE,
            usdValue = ALGO_TO_USD_CONVERSION_RATE
        )
        assertEquals(expected, result)
    }

    companion object {
        private val ALGO_AMOUNT = fixtureOf<BigInteger>()
        private val PRIMARY_ALGO_PARITY_VALUE = fixtureOf<ParityValue>()
        private val SECONDARY_ALGO_PARITY_VALUE = fixtureOf<ParityValue>()
        private val ALGO_TO_USD_CONVERSION_RATE = fixtureOf<BigDecimal>()
    }
}
