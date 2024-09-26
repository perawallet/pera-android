package com.algorand.android.module.parity.domain.usecase.implementation

import org.junit.Test

class CalculateParityValueUseCaseTest {

    private val sut = CalculateParityValueUseCase()

    @Test
    fun `EXPECT ParityValue`() {
        // Arrange
        val assetUsdValue = 5.toBigDecimal()
        val assetDecimals = 6
        val amount = 1_000_000.toBigInteger()
        val conversionRate = 1.toBigDecimal()
        val currencySymbol = "USD"

        // Act
        val result = sut(assetUsdValue, assetDecimals, amount, conversionRate, currencySymbol)

        // Assert
        assert(result.amountAsCurrency.compareTo(5.000000.toBigDecimal()) == 0)
        assert(result.selectedCurrencySymbol == "USD")
    }
}
