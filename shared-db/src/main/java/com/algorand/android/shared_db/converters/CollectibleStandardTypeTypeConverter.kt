package com.algorand.android.shared_db.converters

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleStandardTypeEntity

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
