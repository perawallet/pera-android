package com.algorand.android.parity.domain.usecase.primary.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.parity.domain.repository.ParityRepository
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class GetPrimaryCurrencySymbolUseCaseTest {

    private val parityRepository: ParityRepository = mock()

    private val sut = GetPrimaryCurrencySymbolUseCase(parityRepository)

    @Test
    fun `EXPECT symbol WHEN it is cached`() {
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(
            CacheResult.Success.create(SELECTED_CURRENCY_DETAIL)
        )

        val result = sut()


        assertEquals(USD_SYMBOL, result)
    }

    @Test
    fun `EXPECT null WHEN symbol is not cached`() {
        whenever(parityRepository.getSelectedCurrencyDetail()).thenReturn(CacheResult.Error.create(Throwable()))

        val result = sut()

        assertNull(result)
    }

    companion object {
        private const val USD_SYMBOL = "USD"
        private val SELECTED_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>().copy(
            currencySymbol = USD_SYMBOL
        )
    }
}
