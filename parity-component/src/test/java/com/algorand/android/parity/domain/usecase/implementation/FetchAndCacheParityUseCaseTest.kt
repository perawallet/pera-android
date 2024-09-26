package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.caching.CacheResult
import com.algorand.android.currency.domain.usecase.*
import com.algorand.android.module.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.module.parity.domain.repository.ParityRepository
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class FetchAndCacheParityUseCaseTest {

    private val getPrimaryCurrencyId: GetPrimaryCurrencyId = mock()
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo = mock()
    private val parityRepository: ParityRepository = mock()

    private val sut = FetchAndCacheParityUseCase(
        getPrimaryCurrencyId = getPrimaryCurrencyId,
        isPrimaryCurrencyAlgo = isPrimaryCurrencyAlgo,
        parityRepository = parityRepository
    )

    @Test
    fun `EXPECT USD to be cached WHEN primary currency is ALGO`() = runTest {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(true)
        whenever(parityRepository.fetchAndCacheParity(USD_CURRENCY_ID))
            .thenReturn(CacheResult.Success.create(USD_CURRENCY_DETAIL))

        val result = sut()

        assertEquals((result as CacheResult.Success).data.currencyId, USD_CURRENCY_ID)
    }

    @Test
    fun `EXPECT selected currency to be cached WHEN primary currency is not ALGO`() = runTest {
        whenever(isPrimaryCurrencyAlgo()).thenReturn(false)
        whenever(getPrimaryCurrencyId()).thenReturn(TRY_CURRENCY_ID)
        whenever(parityRepository.fetchAndCacheParity(TRY_CURRENCY_ID))
            .thenReturn(CacheResult.Success.create(TRY_CURRENCY_DETAIL))

        val result = sut()

        assertEquals((result as CacheResult.Success).data.currencyId, TRY_CURRENCY_ID)
    }

    companion object {
        private const val USD_CURRENCY_ID = "USD"
        private const val TRY_CURRENCY_ID = "TRY"
        private val USD_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>().copy(
            currencyId = USD_CURRENCY_ID
        )
        private val TRY_CURRENCY_DETAIL = fixtureOf<SelectedCurrencyDetail>().copy(
            currencyId = TRY_CURRENCY_ID
        )
    }
}
