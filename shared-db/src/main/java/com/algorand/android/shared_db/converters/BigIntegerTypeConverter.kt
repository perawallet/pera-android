package com.algorand.android.shared_db.converters

import androidx.room.*
import java.math.BigInteger

@ProvidedTypeConverter
internal object BigIntegerTypeConverter {

    @TypeConverter
    fun bigIntegerToString(value: BigInteger): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToBigInteger(value: String): BigInteger {
        return value.toBigInteger()
    }
}