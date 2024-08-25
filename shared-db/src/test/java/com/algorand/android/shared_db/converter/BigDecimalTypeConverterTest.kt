package com.algorand.android.account.core.data.database.converter

import com.algorand.android.shared_db.converters.BigDecimalTypeConverter
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class BigDecimalTypeConverterTest {

    @Test
    fun `EXPECT stringToBigDecimal to return null when value is null`() {
        val result = BigDecimalTypeConverter.stringToBigDecimal(null)

        assertNull(result)
    }

    @Test
    fun `EXPECT stringToBigDecimal to return BigDecimal when value is not null`() {
        val result = BigDecimalTypeConverter.stringToBigDecimal("1.0214")

        assertEquals(BigDecimal("1.0214"), result)
    }

    @Test
    fun `EXPECT bigDecimalToString to return null when bigDecimal is null`() {
        val result = BigDecimalTypeConverter.bigDecimalToString(null)

        assertNull(result)
    }

    @Test
    fun `EXPECT bigDecimalToString to return String when bigDecimal is not null`() {
        val result = BigDecimalTypeConverter.bigDecimalToString(BigDecimal("1231.2141551121"))

        assertEquals("1231.2141551121", result)
    }
}
