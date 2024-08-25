package com.algorand.android.account.core.data.database.converter

import com.algorand.android.shared_db.converters.BigIntegerTypeConverter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class BigIntegerTypeConverterTest {

    @Test
    fun `EXPECT string to be converted successfully`() {
        val input = "1241229314999512512124214"

        val result = BigIntegerTypeConverter.stringToBigInteger(input)

        val expected = BigInteger("1241229314999512512124214")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT big integer to be converted successfully`() {
        val input = BigInteger("1241229314999512512124214")

        val result = BigIntegerTypeConverter.bigIntegerToString(input)

        val expected = "1241229314999512512124214"
        assertEquals(expected, result)
    }
}
