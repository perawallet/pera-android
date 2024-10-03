package com.algorand.android.module.shareddb.converter

import com.algorand.android.module.shareddb.converters.BigDecimalTypeConverter
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
