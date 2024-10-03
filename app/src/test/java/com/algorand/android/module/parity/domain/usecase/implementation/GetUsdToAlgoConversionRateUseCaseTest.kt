package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.caching.CacheResult
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.testutil.fixtureOf
import java.math.BigDecimal
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetUsdToAlgoConversionRateUseCaseTest {

    private val parityRepository: ParityRepository = mock()

    private val sut = GetUsdToAlgoConversionRateUseCase(parityRepository)

    @Test
    fun `EXPECT zero WHEN selected currency detail is null`() {
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(null)

        val result = sut()

        Assert.assertTrue(result.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `EXPECT zero WHEN algo to selected currency conversion rate is zero`() {
        whenever(parityRepository.getSelectedCurrencyDetail())
            .thenReturn(
                CacheResult.Success.create(
                    SELECTED_CURRENCY_DETAIL.copy(algoToSelectedCurrencyConversionRate = BigDecimal.ZERO)
                )
            )

        val result = sut()

        Assert.assertTrue(result.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `EXPECT zero WHEN algo to selected currency conversion rate is null`() {
        whenever(parityRepository.getSelectedCurrencyDetail())
            .thenReturn(
                CacheResult.Success.create(
                    SELECTED_CURRENCY_DETAIL.copy(algoToSelectedCurrencyConversionRate = null)
                )
            )

        val result = sut()

        Assert.assertTrue(result.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `EXPECT zero WHEN algo to selected currency rate is not null but usd to selected currency is null`() {
        whenever(parityRepository.getSelectedCurrencyDetail())
            .thenReturn(
                CacheResult.Success.create(
                    SELECTED_CURRENCY_DETAIL.copy(
                        algoToSelectedCurrencyConversionRate = BigDecimal.ONE,
                        usdToSelectedCurrencyConversionRate = null
                    )
                )
            )

        val result = sut()

        Assert.assertTrue(result.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `EXPECT conversion rate WHEN algo to selected currency conversion rate is not zero or null`() {
        whenever(parityRepository.getSelectedCurrencyDetail())
            .thenReturn(
                CacheResult.Success.create(
                    SELECTED_CURRENCY_DETAIL.copy(
                        algoToSelectedCurrencyConversionRate = BigDecimal.ONE,
                        usdToSelectedCurrencyConversionRate = BigDecimal.TEN
                    )
                )
            )

        val result = sut()

        Assert.assertTrue(result.compareTo(BigDecimal.TEN) == 0)
    }

    companion object {
        private val SELECTED_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>()
    }
}
