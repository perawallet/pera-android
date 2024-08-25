package com.algorand.android.shared_db.converters

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity

@ProvidedTypeConverter
internal object CollectibleMediaTypeTypeConverter {

    @TypeConverter
    fun collectibleMediaTypeToString(input: CollectibleMediaTypeEntity): String {
        return input.toString()
    }

    @TypeConverter
    fun stringToCollectibleMediaType(input: String): CollectibleMediaTypeEntity {
        return CollectibleMediaTypeEntity.valueOf(input)
    }
}
