package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.module.caching.CacheResult.Success.Companion.create
import com.algorand.android.module.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class GetPrimaryCurrencyNameUseCaseTest {

    private val parityRepository: ParityRepository = mock()
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId = mock()

    private val sut = GetPrimaryCurrencyNameUseCase(
        parityRepository,
        getPrimaryCurrencyId
    )

    @Test
    fun `EXPECT primary currency symbol WHEN selected currency detail is success`() {
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(create(SELECTED_CURRENCY_DETAIL))

        val result = sut()

        assertEquals(SELECTED_CURRENCY_DETAIL.currencySymbol, result)
    }

    @Test
    fun `EXPECT primary currency id WHEN selected currency detail is not success`() {
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(null)
        whenever(getPrimaryCurrencyId()).thenReturn(PRIMARY_CURRENCY_ID)

        val result = sut()

        assertEquals(PRIMARY_CURRENCY_ID, result)
    }

    private companion object {
        private const val PRIMARY_CURRENCY_ID = "ALGO"
        val SELECTED_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>()
    }
}
