package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

class GetUsdToPrimaryCurrencyConversionRateUseCaseTest {

    private val parityRepository: ParityRepository = mock()
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mock()

    private val sut = GetUsdToPrimaryCurrencyConversionRateUseCase(parityRepository, isPrimaryCurrencyAlgo)

    @Test
    fun `EXPECT usdToSelectedCurrencyConversionRate WHEN primary currency is Algo`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(true)
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(
            CacheResult.Success.create(
                SELECTED_CURRENCY_DETAIL.copy(usdToSelectedCurrencyConversionRate = BigDecimal.TEN)
            )
        )

        // Act
        val result = sut()

        // Assert
        assertEquals(result.compareTo(BigDecimal.TEN), 0)
    }

    @Test
    fun `EXPECT algoToSelectedCurrencyConversionRate WHEN primary currency is not Algo`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(false)
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(
            CacheResult.Success.create(
                SELECTED_CURRENCY_DETAIL.copy(algoToSelectedCurrencyConversionRate = BigDecimal.ONE)
            )
        )

        // Act
        val result = sut()

        // Assert
        assertEquals(result.compareTo(BigDecimal.ONE), 0)
    }

    @Test
    fun `EXPECT zero WHEN selected currency detail is null`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(false)
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(CacheResult.Error.create(Throwable()))

        // Act
        val result = sut()

        // Assert
        assertEquals(result.compareTo(BigDecimal.ZERO), 0)
    }

    companion object {
        private val SELECTED_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>()
    }
}
