package com.algorand.android.module.shareddb.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.math.BigDecimal

@ProvidedTypeConverter
internal object BigDecimalTypeConverter {

    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? {
        return value?.toBigDecimalOrNull()
    }

    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toString()
    }
}
