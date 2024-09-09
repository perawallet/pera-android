package com.algorand.android.parity.domain.usecase.secondary

import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.parity.domain.usecase.secondary.implementation.GetSecondaryCurrencySymbolUseCase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class GetSecondaryCurrencySymbolUseCaseTest {

    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mock()

    private val sut = GetSecondaryCurrencySymbolUseCase(isPrimaryCurrencyAlgo)

    @Test
    fun `EXPECT usd WHEN primary currency is ALGO`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(true)

        val result = sut()

        assertEquals("$", result)
    }

    @Test
    fun `EXPECT algo symbol WHEN primary currency is not ALGO`() {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(false)

        val result = sut()

        assertEquals("\u00A6", result)
    }
}
