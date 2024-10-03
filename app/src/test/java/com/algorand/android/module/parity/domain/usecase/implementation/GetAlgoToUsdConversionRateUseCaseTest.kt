package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.parity.domain.usecase.GetUsdToAlgoConversionRate
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

class GetAlgoToUsdConversionRateUseCaseTest {

    private val getUsdToAlgoConversionRate: GetUsdToAlgoConversionRate = mock()

    private val sut = GetAlgoToUsdConversionRateUseCase(getUsdToAlgoConversionRate)

    @Test
    fun `EXPECT zero WHEN usd to algo conversion rate is zero`() {
        whenever(getUsdToAlgoConversionRate()).thenReturn(BigDecimal.ZERO)

        val result = sut()

        assertTrue(result.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `EXPECT conversion rate WHEN usd to algo conversion rate is not zero`() {
        whenever(getUsdToAlgoConversionRate()).thenReturn(BigDecimal.TEN)

        val result = sut()

        assertTrue(result.compareTo(0.1.toBigDecimal()) == 0)
    }
}
