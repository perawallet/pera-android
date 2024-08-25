package com.algorand.android.shared_db.converters

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity

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
