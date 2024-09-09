package com.algorand.android.parity.data.mapper

import com.algorand.android.parity.data.model.CurrencyDetailResponse
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class SelectedCurrencyDetailMapperImplTest {

    private val sut = SelectedCurrencyDetailMapperImpl()

    @Test
    fun `EXPECT ALGO currency id WHEN primary currency is ALGO`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE, true)

        assertEquals(ALGO_CURRENCY_ID, result.currencyId)
    }

    @Test
    fun `EXPECT currency id from response WHEN primary currency is not ALGO`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE, false)

        assertEquals(CURRENCY_DETAIL_RESPONSE.id, result.currencyId)
    }

    @Test
    fun `EXPECT ALGO id as name WHEN primary currency is ALGO`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE, true)

        assertEquals(ALGO_CURRENCY_ID, result.currencyName)
    }

    @Test
    fun `EXPECT currency name from response WHEN primary currency is not ALGO and name in response is not null`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(name = "name")

        val result = sut(response, false)

        assertEquals("name", result.currencyName)
    }

    @Test
    fun `EXPECT empty name WHEN primary currency is not ALGO and name in response is null`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(name = null)

        val result = sut(response, false)

        assertEquals("", result.currencyName)
    }

    @Test
    fun `EXPECT currency symbol from response WHEN primary currency is not ALGO`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(symbol = "$")

        val result = sut(response, false)

        assertEquals("$", result.currencySymbol)
    }

    @Test
    fun `EXPECT empty symbol WHEN primary currency is not ALGO and symbol in response is null`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE.copy(symbol = null), false)

        assertEquals("", result.currencySymbol)
    }

    @Test
    fun `EXPECT ALGO symbol WHEN primary currency is ALGO`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE, true)

        assertEquals(ALGO_SYMBOL, result.currencySymbol)
    }

    @Test
    fun `EXPECT 1 for algo to selected currency WHEN primary currency is ALGO`() {
        val result = sut(CURRENCY_DETAIL_RESPONSE, true)

        assertEquals(result.algoToSelectedCurrencyConversionRate?.compareTo(BigDecimal.ONE), 0)
    }

    @Test
    fun `EXPECT exchange price for algo to selected currency WHEN primary currency is not ALGO`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(exchangePrice = "0.5")

        val result = sut(response, false)

        assertEquals(result.algoToSelectedCurrencyConversionRate?.compareTo(0.5.toBigDecimal()), 0)
    }

    @Test
    fun `EXPECT null WHEN response exchange price is invalid`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(exchangePrice = "invalid")

        val result = sut(response, false)

        assertNull(result.algoToSelectedCurrencyConversionRate)
    }

    @Test
    fun `EXPECT usdValue for usd to selected currency WHEN primary currency is not ALGO`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(usdValue = BigDecimal.TEN)

        val result = sut(response, false)

        assertEquals(result.usdToSelectedCurrencyConversionRate?.compareTo(BigDecimal.TEN), 0)
    }

    @Test
    fun `EXPECT zero for usd to selected currency WHEN primary currency is ALGO and exchange price is null`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(exchangePrice = null)

        val result = sut(response, true)

        assertEquals(result.usdToSelectedCurrencyConversionRate?.compareTo(BigDecimal.ZERO), 0)
    }

    @Test
    fun `EXPECT zero for usd to selected currency WHEN primary currency is ALGO and exchange price is zero`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(exchangePrice = "0")

        val result = sut(response, true)

        assertEquals(result.usdToSelectedCurrencyConversionRate?.compareTo(BigDecimal.ZERO), 0)
    }

    @Test
    fun `EXPECT ratio for usd to selected currency WHEN primary currency is ALGO and exchange price is not null nor zero`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(
            exchangePrice = "2",
            usdValue = BigDecimal.TEN
        )

        val result = sut(response, true)

        assertEquals(result.usdToSelectedCurrencyConversionRate?.compareTo(BigDecimal(5)), 0)
    }

    @Test
    fun `EXPECT zero for usd to selected currency WHEN primary currency is ALGO and usd value is null`() {
        val response = CURRENCY_DETAIL_RESPONSE.copy(usdValue = null)

        val result = sut(response, true)

        assertEquals(result.usdToSelectedCurrencyConversionRate?.compareTo(BigDecimal.ZERO), 0)
    }

    companion object {
        private const val ALGO_CURRENCY_ID = "ALGO"
        private const val ALGO_SYMBOL = "\u00A6"
        private val CURRENCY_DETAIL_RESPONSE = fixtureOf<CurrencyDetailResponse>()
    }
}
