package com.algorand.android.module.shareddb.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
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
