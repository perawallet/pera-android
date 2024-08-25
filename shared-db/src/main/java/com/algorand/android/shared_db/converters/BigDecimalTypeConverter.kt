package com.algorand.android.shared_db.converters

import androidx.room.*
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
