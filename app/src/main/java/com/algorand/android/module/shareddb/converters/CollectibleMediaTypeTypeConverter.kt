package com.algorand.android.module.shareddb.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity

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
