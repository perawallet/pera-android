package com.algorand.android.module.shareddb.converters

import androidx.room.*
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity

@ProvidedTypeConverter
internal object CollectibleStandardTypeTypeConverter {

    @TypeConverter
    fun collectibleStandardTypeToString(input: CollectibleStandardTypeEntity): String {
        return input.toString()
    }

    @TypeConverter
    fun stringToCollectibleStandardType(input: String): CollectibleStandardTypeEntity {
        return CollectibleStandardTypeEntity.valueOf(input)
    }
}
