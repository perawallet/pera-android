package com.algorand.android.module.shareddb.converters

import androidx.room.*
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity

@ProvidedTypeConverter
internal object CollectibleMediaTypeExtensionTypeConverter {

    @TypeConverter
    fun collectibleMediaTypeExtensionToString(input: CollectibleMediaTypeExtensionEntity): String {
        return input.toString()
    }

    @TypeConverter
    fun stringToCollectibleMediaTypeExtension(input: String): CollectibleMediaTypeExtensionEntity {
        return CollectibleMediaTypeExtensionEntity.valueOf(input)
    }
}
