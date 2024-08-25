package com.algorand.android.parity.domain.usecase.secondary

import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.parity.domain.usecase.secondary.implementation.GetUsdToSecondaryCurrencyConversionRateUseCase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

class GetUsdToSecondaryCurrencyConversionRateUseCaseTest {

    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate = mock()
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mock()

    private val sut = GetUsdToSecondaryCurrencyConversionRateUseCase(
        getUsdToAlgoConversionRate,
        isPrimaryCurrencyAlgo
    )

    @Test
    fun `EXPECT 1 WHEN primary currency is ALGO`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(true)

        val result = sut()

        assertEquals(result.compareTo(BigDecimal.ONE), 0)
    }

    @Test
    fun `EXPECT conversion rate WHEN primary currency is not ALGO`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(false)
        whenever(getUsdToAlgoConversionRate()).thenReturn(BigDecimal(0.5))

        val result = sut()

        assertEquals(result.compareTo(BigDecimal(0.5)), 0)
    }
}
